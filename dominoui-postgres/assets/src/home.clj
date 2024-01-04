(ns <<ns-name>>.web.views.home
    (:require
      [<<ns-name>>.web.domino :as domino]
      [<<ns-name>>.web.htmx :refer [page-htmx]]
      [<<ns-name>>.web.views.disp.home :as disp.home]
      [simpleui.core :as simpleui :refer [defcomponent]]))

(defcomponent ^:endpoint bmi-form [{:keys [session] :as req} ^:double height ^:double weight]
  (cond
   height (domino/transact session :height height)
   weight (domino/transact session :weight weight)
   :else (disp.home/form
          (domino/select session :height)
          (domino/select session :weight)
          (domino/select session :bmi))))

(defn ui-routes [{:keys [query-fn]}]
  (simpleui/make-routes
   ""
   [query-fn]
   (fn [req]
     (let [req (assoc req :query-fn query-fn)
           session (or (not-empty (:session req)) (domino/initial-session req))
           req (assoc req :session session)]
       (-> req bmi-form page-htmx (assoc :session session))))))

