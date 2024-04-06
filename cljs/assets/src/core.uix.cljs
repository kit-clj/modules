(ns <<ns-name>>.core
  (:require
   [uix.core :refer [defui $]]
   [uix.dom]))

;; -------------------------
;; Views

(defui home-page []
  ($ :div
     ($ :h2 "Welcome to UIx!")))

;; -------------------------
;; Initialize app

(defn ^:dev/after-load mount-root []
  (uix.dom/render-root ($ home-page) (uix.dom/create-root (js/document.getElementById "app"))))

(defn ^:export ^:dev/once init! []
  (mount-root))
