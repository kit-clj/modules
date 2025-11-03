(ns <<ns-name>>.web.htmx
  (:require
   [ring.util.http-response :as http-response]
   [hiccup.core :as h]
   [hiccup.page :as p]))

(defmacro page [opts & content]
  `(-> (p/html5 ~opts ~@content)
       http-response/ok
       (http-response/content-type "text/html")))

(defmacro fragment [opts & content]
  `(-> (str (h/html ~opts ~@content))
       http-response/ok
       (http-response/content-type "text/html")))
