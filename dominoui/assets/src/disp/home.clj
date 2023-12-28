(ns <<ns-name>>.web.views.disp.home)

(defn input [label name value]
  [:div
   [:label label]
   [:input
    {:type "text" :name name :value value
     :hx-post "bmi-form" :hx-trigger "keyup changed delay:0.3s"}]])

(defn bmi-label [bmi]
  [:label#bmi (format "BMI: %.1f" bmi)])

(defn form [height weight bmi]
  [:div
   [:h2 "BMI Calculator"]
   (input "height (KG) " "height" height)
   (input "weight (M) " "weight" weight)
   (bmi-label bmi)])
