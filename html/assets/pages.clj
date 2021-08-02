(ns <<ns-name>>web.routes.pages
  (:require
    [<<ns-name>>.web.middleware.exception :as exception]
    [<<ns-name>>.web.pages.layout :as layout]
    [integrant.core :as ig]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [reitit.ring.middleware.parameters :as parameters]
    [ring.middleware.defaults :as defaults]
    [ring-ttl-session.core :refer [ttl-memory-store]]))

(defn wrap-site-defaults []
  (let [page (layout/error-page
                {:status 403
                  :title "Invalid anti-forgery token"})]
    (fn [handler]
      (defaults/wrap-defaults
        handler
        (-> defaults/site-defaults
            (assoc-in [:security :anti-forgery] {:error-response page})
            (assoc-in [:session :store] (ttl-memory-store (* 60 30))))))))

(defn home [request]
  (layout/render request "home.html"))

;; Routes
(defn page-routes [base-path]
  [base-path
   ["/" {:get home}]])

(defn route-data
  [{:keys [template-opts]}]
  {:swagger    {:id ::api}
   :middleware [;; Ring site defaults
                (wrap-site-defaults)
                ;; query-params & form-params
                parameters/parameters-middleware
                ;; encoding response body
                muuntaja/format-response-middleware                
                ;; exception handling
                exception/wrap-exception]})

(derive :reitit.routes/pages :reitit/routes)

(defmethod ig/init-key :reitit.routes/pages
  [_ {:keys [base-path]
      :or   {base-path ""}
      :as   opts}]
  (layout/init-custom-tags!)
  ["" (route-data opts) (page-routes base-path)])

