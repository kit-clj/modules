(ns <<ns-name>>.datomic
  (:require [clojure.tools.logging :as log]
            [datomic.api :as d]
            [integrant.core :as ig]))

(defmethod ig/init-key :db.datomic/conn
  [_ {:keys [db-uri]}]
  (when (d/create-database db-uri)
    (log/info (str "Database " db-uri " created (didn't exist)")))
  (d/connect db-uri))

(defmethod ig/halt-key! :db.datomic/conn
  [_ conn]
  (d/release conn))
