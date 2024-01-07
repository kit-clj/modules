(ns <<ns-name>>.web.domino
    (:require
      [domino.core :as domino]
      [simpleui.response :as response]
      [<<ns-name>>.web.controllers.user :as user]
      [<<ns-name>>.web.ws :as ws]
      [<<ns-name>>.web.views.disp.home :as disp.home]))

;; adapted from domino README.md

(defn- schema [{:keys [query-fn]}]
  {:model   [[:demographics
              [:height {:id :height}]
              [:weight {:id :weight}]]
             [:vitals
              [:bmi {:id :bmi}]]]
   :events  [{:inputs  [:height :weight]
              :outputs [:bmi]
              :handler (fn [{{:keys [height weight]} :inputs
                             {:keys [bmi]} :outputs}]
                         {:bmi (if (and height weight)
                                 (/ weight (* height height))
                                 bmi)})}]
   :effects [{:inputs [:bmi]
              :handler (fn [{{:keys [bmi]} :inputs}]
                         (ws/broadcast
                          (disp.home/bmi-label bmi)))}
             {:inputs [:weight]
              :handler (fn [{{:keys [weight]} :inputs}]
                         (user/set-weight query-fn weight))}
             {:inputs [:height]
              :handler (fn [{{:keys [height]} :inputs}]
                         (user/set-height query-fn height))}]})

(defn initial-session [req]
  (let [{:keys [height weight]} (user/get-user req)]
    (domino/initialize (schema req) {:demographics {:height (or height 1.6)
                                                    :weight (or weight 60.0)}})))

(defn transact [session id v]
  (assoc response/no-content
         :session (domino/transact session [[[id] v]])))

(defn select [session id]
  (domino/select session id))
