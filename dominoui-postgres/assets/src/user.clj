(ns a.b.web.controllers.user)

(def USER-ID 1)

(defn get-user [{:keys [query-fn]}]
  (query-fn :user-by-id {:user-id USER-ID}))

(defn set-weight [{:keys [query-fn]} weight]
  (query-fn :set-weight {:user-id USER-ID :weight weight}))

(defn set-height [{:keys [query-fn]} height]
  (query-fn :set-height {:user-id USER-ID :height height}))
