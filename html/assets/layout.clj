(ns <<ns-name>>.web.pages.layout
  (:require
   [clojure.java.io]
   [selmer.parser :as parser]
   [ring.util.http-response :refer [content-type ok]]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
   [ring.util.response]))

(defn init-custom-tags!
 []
 (parser/add-tag! :csrf-field (fn [_ _] (anti-forgery-field))))

(defn render
 [request template & [params]]
 ((get-in request [:reitit.core/match :data :selmer :render-file]) template
   (assoc params
     :page template
     :csrf-token *anti-forgery-token*)))

(defn error-page
  "error-details should be a map containing the following keys:
   :status - error status
   :title - error title (optional)
   :message - detailed error message (optional)
   returns a response map with the error page as the body
   and the status specified by the status key"
  [{:keys [render-file]} error-details]
  {:status  (:status error-details)
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body    (render-file "error.html" error-details)})