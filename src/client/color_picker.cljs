(ns client.color-picker
  (:require
   [re-frame.core :as re-frame]
   [clojure.string :as str]))


(comment
  (def db @re-frame.db/app-db)
  )


(re-frame/reg-event-db
 ::selected-color
 (fn [db [_ r g]]
   (-> db
       (assoc-in [:color :r] r)
       (assoc-in [:color :g] g))))


(re-frame/reg-sub
 ::selected-color
 (fn [db _]
   (get db :color)))


(re-frame/reg-event-db
 ::show-picker?
 (fn [db [_ b]]
   (assoc db ::show-picker? b)))


(re-frame/reg-sub
 ::show-picker?
 (fn [db _]
   (get db ::show-picker?)))


(defn rgb-str [r g]
  (str "rgb(" (str/join "," [r g 0]) ")"))


(defn pixel [r g]
  [:div {:style {:width "5px" :height "5px"
                 :background-color (rgb-str r g)}
         :on-click #(re-frame/dispatch [::selected-color r g])}])


(defn pallet []
  [:div {:style {:width "255px" :height "255px"
                 :display "flex" :flex-wrap "wrap"}}
   (for [r (range 0 255 5)]
     (for [g (range 0 255 5)]
       ^{:key (str r g)}
       [pixel r g]))])


(defn selected-color []
  (let [{:keys [r g]}
        @(re-frame/subscribe [::selected-color])]
    [:div {:style {:width "30px"
                   :height "30px"
                   :margin-left "5px"
                   :background-color (rgb-str r g)
                   :border-style "solid"
                   :border-width "2px"}}]))


(defn ui []
  (let [show-picker? @(re-frame/subscribe [::show-picker?])]
    (when show-picker?
      [:div
       [:div {:style {:display "flex"}}
        [pallet]
        [selected-color]]
       [:button.btn.btn-default
        {:on-click #(re-frame/dispatch [::show-picker? false])
         :style {:margin-top "5px" :width "100%"}}
        "Done"]])))
