(ns client.color-picker
  (:require
   [clojure.string :as str]
   [system.shared :as shared]))


(defn pixel [on-color-click r g]
  [:div {:style {:width "5px" :height "5px"
                 :background-color (shared/rgb-str r g)}
         :on-click #(on-color-click {:color/r r
                                     :color/g g
                                     :color/b 0})}])

(defn pallet [on-color-click]
  [:div {:id "color-picker-pallet"
         :style {:width "255px" :height "255px"
                 :display "flex" :flex-wrap "wrap"}}
   (for [r (range 0 255 5)
         g (range 0 255 5)]
     ^{:key (shared/rgb-str r g)}
     [pixel on-color-click r g])])

(defn selected-color [{:color/keys [r g]}]
  [:div
   {:style {:width "30px"
            :height "30px"
            :margin-left "5px"
            :background-color (shared/rgb-str r g)
            :border-style "solid"
            :border-width "2px"}}])

(defn color-picker [{:keys [on-color-click color on-done] :as props}]
  [:div
   [:div {:style {:display "flex"}}
    [pallet on-color-click]
    [selected-color color]]
   [:button.btn.btn-default
    {:id "color-picker-done-button"
     :on-click on-done
     :style {:margin-top "5px" :margin-bottom "5px" :width "100%"}}
    "Done"]])
