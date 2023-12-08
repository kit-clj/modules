(ns <<ns-name>>.web.views.hello
    (:require
      [simpleui.core :as simpleui :refer [defcomponent]]
      [<<ns-name>>.web.htmx :refer [page-htmx]]))

(defcomponent ^:endpoint hello [req my-name]
  [:div#hello "Hello " my-name])

(defn ui-routes [base-path]
  (simpleui/make-routes
   base-path
   (fn [req]
     (page-htmx
      [:label {:style "margin-right: 10px"}
       "What is your name?"]
      [:input {:type "text"
               :name "my-name"
               :hx-patch "hello"
               :hx-target "#hello"
               :hx-swap "outerHTML"}]
      (hello req "")))))
