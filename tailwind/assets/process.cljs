(ns autogen.process
  (:require
   ["fs" :as fs]
   cljs.pprint
   [clojure.edn :as edn]
   [clojure.string :as string]
   [nbb.core :refer [slurp]]
   [promesa.core :as p]))

(defn vec-walk [f s]
  (cond
    (vector? s) (f s)
    (seq? s) (map #(vec-walk f %) s)
    :else s))

(defn pprint [s]
  (with-out-str
    (cljs.pprint/pprint s)))

(defn- format [fmt-str & substitutes]
  (reduce #(.replace %1 "%s" %2) fmt-str substitutes))
(defn- map-str [f s]
  (->> s
       (map f)
       (string/join "\n")))

(defn- tag->classes [t]
  (string/join " "
               (some-> t name (.split ".") rest)))
(defn- get-tag [t]
  (re-find #"\w+" (name t)))
(defn- indent-str [i]
  (string/join (repeat i "  ")))

(defn- ensure-string [s]
  (when (string? s)
    s))
(defn- get-class [m]
  (cond
    (map? m)
    (-> m :class ensure-string)
    (seq? m)
    (->> m
         (drop-while #(not= :class %))
         second
         ensure-string)))

(declare insert-maps)
(defn- insert-map [[tag m & children]]
  (if (or (map? m) (get-class m))
    (vec
     (list* tag m (map insert-maps children)))
    (vec
     (list* tag {} (insert-maps m) (map insert-maps children)))))
(defn- insert-maps [hiccup]
  (vec-walk insert-map hiccup))

(defn hiccup-vector? [[kw m]]
  (and
   (keyword? kw)
   (or
    (-> kw name (.includes "."))
    (get-class m))))

(declare render-hiccup)
(defn- render-vector [indent [tag m & children]]
  (let [tag-str (get-tag tag)
        classes (.trim (str (tag->classes tag) " " (get-class m)))]
    (str
     (indent-str indent)
     "<"
     tag-str
     " class=\""
     classes
     "\">\n"
     (render-hiccup (inc indent) children)
     "\n"
     (indent-str indent)
     "</"
     tag-str
     ">")))

(defn- render-hiccup [indent s]
  (cond
    (and (vector? s) (hiccup-vector? s)) (render-vector indent s)
    (coll? s) (map-str #(render-hiccup indent %) s)
    :else (str (indent-str indent) s)))

(defn html [s]
  (format "<!--\n\n%s-->\n%s"
          (pprint s)
          (->> s insert-maps (render-hiccup 0))))

(defn skip-macros [s]
  (-> s
      (.replaceAll "::" ":")
      (.replaceAll "`" "")
      (.replaceAll "~" "")
      (.replaceAll "@" "")
      (.replaceAll "#(" "(")))

(defn read-src [f]
  (p/let [s (slurp f)
          s (skip-macros s)]
    (try
      (edn/read-string
       (str "[" s "]"))
      (catch js/Error e
        (println "Warning:" e "in" f)))))

(defn get-hiccup* [m]
  (cond
    (and (vector? m) (hiccup-vector? m))
    (cons m (mapcat get-hiccup* (rest m)))
    (coll? m) (mapcat get-hiccup* m)))

(defn get-hiccup [f]
  (p/let [s (read-src f)]
    (get-hiccup* s)))

(defn spit [f s]
  (.writeFile
    fs
    (format "target/generated-sources/tailwind/auto_%s.html" (.replaceAll f "/" "_"))
    s
    #()))

(defn process-file [f]
  (p/let [hiccup (get-hiccup f)]
    (some->> hiccup not-empty html (spit f))))
