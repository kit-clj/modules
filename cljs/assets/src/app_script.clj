(ns <<ns-name>>.web.pages.app-script
  "Selmer tag {% app-script %}: the path of the compiled app module.
  Release builds name the module after its content hash (shadow-cljs
  :module-hash-names), so the path is read from the build manifest."
  (:require
    [<<ns-name>>.env :as env]
    [clojure.edn :as edn]
    [clojure.java.io :as io]
    [selmer.parser :as parser]))

(def ^:private manifest-resource "public/js/manifest.edn")

(defn- release-app-script []
  (let [manifest (or (io/resource manifest-resource)
                     (throw (ex-info "Missing ClojureScript build manifest"
                                     {:resource manifest-resource})))
        module   (->> (edn/read-string (slurp manifest))
                      (some #(when (= :app (:module-id %)) %)))]
    (str "/js/" (:output-name module))))

(def ^:private app-script
  (delay (if (= :prod (get-in env/defaults [:opts :profile]))
           (release-app-script)
           "/js/app.js")))

(parser/add-tag! :app-script (fn [_ _] @app-script))
