(ns client.view-b
  (:require
   [client.events :as events]
   [re-frame.core :as rf]))


(defn sums [total-cost total-weight]
  [:<>
   [:div "Total weight: " total-weight]
   [:div "Total cost: " total-cost]])

(defn row [{:box/keys [color id name weight]
            :keys [shipping-cost background-color]
            :as box}]
  (let [{:color/keys [r g]} color]
    [:tr {:id id}
     [:td name]
     [:td (str weight " kilograms")]
     [:td {:style {:background-color background-color}}]
     [:td (str shipping-cost " Sek")]]))

(defn table []
  (let [boxes        @(rf/subscribe [:boxes])
        total-cost   @(rf/subscribe [::events/total-cost])
        total-weight @(rf/subscribe [::events/total-weight])]
    [:div {:style {:width "100%"}}
     [:table.table {:style {:width "100%"}}
      [:thead
       [:tr
        [:th "Receiver"]
        [:th "Weight"]
        [:th "Box color"]
        [:th "Shipping cost"]]]
      [:tbody
       (for [{:box/keys [id] :as box} boxes]
         ^{:key id}
         [row box])]]
     (when boxes
       [sums total-cost total-weight])]))
