(ns autogen.main
  (:require
    [autogen.process :as process]
    ["node-watch$default" :as watch-files]
    [promesa.core :as p]
    ["recursive-readdir$default" :as recursive]))

(defn tailwind []
  (set! (.-argv js/process)
    #js [nil
         nil
         "-i"
         "./tailwind/input.css"
         "-o"
         "./resources/public/output.css"])
  (js/require "tailwindcss/lib/cli"))

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
                (p/all (map process/process-file files))
                (when watch? (watch path))))))))
      (println "please provide path"))))
