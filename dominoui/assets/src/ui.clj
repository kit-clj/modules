(ns <<ns-name>>.web.routes.ui
  (:require
   [<<ns-name>>.web.middleware.exception :as exception]
   [<<ns-name>>.web.middleware.formats :as formats]
   [<<ns-name>>.web.views.home :as home]
   [<<ns-name>>.web.ws :as ws]
   [integrant.core :as ig]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]))

(defn route-data [opts]
  (merge
   opts
   {:muuntaja   formats/instance
    :middleware
    [;; Default middleware for ui
    ;; query-params & form-params
      parameters/parameters-middleware
      ;; encoding response body
      muuntaja/format-response-middleware
      ;; exception handling
      exception/wrap-exception]}))

(derive :reitit.routes/ui :reitit/routes)

(defmethod ig/init-key :reitit.routes/ui
  [_ {:keys [base-path]
      :or   {base-path ""}
      :as   opts}]
  [base-path (route-data opts)
   (conj (home/ui-routes opts)
         ["/ws"
          (fn [_req]
            {:undertow/websocket
             {:on-open (fn [{:keys [channel]}] (ws/add-channel channel))
              :on-close-message (fn [{:keys [channel]}] (ws/remove-channel channel))}})])])
