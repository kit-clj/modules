(ns <<ns-name>>.web.views.home
    (:require
      [<<ns-name>>.web.domino :as domino]
      [<<ns-name>>.web.htmx :refer [page-htmx]]
      [<<ns-name>>.web.views.disp.home :as disp.home]
      [simpleui.core :as simpleui :refer [defcomponent]]))

(defcomponent ^:endpoint bmi-form [req ^:double height ^:double weight]
  (cond
   height (domino/transact :height height)
   weight (domino/transact :weight weight)
   :else (disp.home/form
          (domino/select :height)
          (domino/select :weight)
          (domino/select :bmi))))

(defn ui-routes [base-path]
  (simpleui/make-routes
   base-path
   (fn [req]
     (assoc
       (page-htmx
        (bmi-form req))
       :session domino/initial-session))))

