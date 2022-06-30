(ns <<ns-name>>.web.htmx
  (:require
    [ring.util.http-response :as http-response]
    [hiccup.core :as h]))


(defn ui [opts & content]
  (-> (h/html opts content)
    http-response/ok
    (http-response/content-type "text/html")))
