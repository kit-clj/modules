(ns a.b.web.controllers.user)

(def USER-ID 1)

(defn get-user [req]
  (prn 'keys (keys req)))