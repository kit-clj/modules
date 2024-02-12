(ns autogen.main
  (:require
    [autogen.process :as process]
    ["node:child_process" :as child-process]
    ["node-watch$default" :as watch-files]
    [promesa.core :as p]
    ["recursive-readdir$default" :as recursive]
    ["fs" :as fs]
))

(defn tailwind []
  (let [cp
        (child-process/spawn
         "npx"
         #js ["tailwindcss" "-i" "./tailwind/input.css" "-o" "./resources/public/output.css"])]
    (-> cp
        .-stdout
        (.on "data" #(-> % .toString js/console.log)))
    (-> cp
        .-stderr
        (.on "data" #(-> % .toString js/console.error)))))

(defn- src? [f]
  (or
   (.endsWith f ".clj")
   (.endsWith f ".cljc")))

(defn- watch [path]
  (tailwind)
  (watch-files
    path
    #js {:recursive true}
    (fn [_evt file]
      (p/do
       (process/process-file file)
       (tailwind)))))

(defn -main [& args]
  (let [{[path] false
         watch? true} (group-by #(contains? #{"-w" "--watch"} %) args)]
    (if path
      (recursive
        path
       (fn [err files]
         (if err
           (js/console.error err)
           (let [files (filter src? files)]
             (if (empty? files)
               (js/console.error "No files found")
               (p/do 
                (.mkdir fs "target/generated-sources/tailwind"
                        #js {:recursive true} #(.error js/console %))
                (p/all (map process/process-file files))
                (when watch? (watch path))))))))
      (println "please provide path"))))
