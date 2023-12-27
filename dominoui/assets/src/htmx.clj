(ns <<ns-name>>.web.htmx
  (:require
   [simpleui.render :as render]
   [ring.util.http-response :as http-response]
   [hiccup.core :as h]
   [hiccup.page :as p]))

(defn page [opts & content]
  (-> (p/html5 opts content)
      http-response/ok
      (http-response/content-type "text/html")))

(defn ui [opts & content]
  (-> (h/html opts content)
      http-response/ok
      (http-response/content-type "text/html")))

(defn page-htmx [& body]
  (page
   [:head
    [:meta {:charset "UTF-8"}]
    [:title "DominoUI + Kit"]]
   [:body {:hx-ext "ws" :ws-connect "/ws"}
    (render/walk-attrs body)
    [:script {:src "https://unpkg.com/htmx.org@1.9.9"}]
    [:script {:src "https://unpkg.com/htmx.org@1.9.9/dist/ext/ws.js"}]]))
