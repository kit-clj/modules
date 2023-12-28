(ns <<ns-name>>.web.middleware.core
  (:require
    [<<ns-name>>.env :as env]
    [ring.middleware.defaults :as defaults]))

(defn wrap-base
  [{:keys [site-defaults-config] :as opts}]
    (fn [handler]
      (-> ((:middleware env/defaults) handler opts)
          (defaults/wrap-defaults site-defaults-config))))
