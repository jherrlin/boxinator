(ns client.view-b
  (:require
   [client.color-picker :as color-picker]
   [client.events :as events]
   [client.routes :as routes]
   [re-com.core :as re-com]
   [client.view-a :as view-a]
   [re-frame.core :as rf]
   [reagent.core :as reagent]
   [system.boxinator :as boxinator]
   [system.country :as country]
   [system.shared :as shared]))


(defn table []
  (let [boxes @(rf/subscribe [:boxes])]
    [:div {:style {:width "100%"}}
     [:table.table {:style {:width "100%"}}
      [:thead
       [:tr
        [:th "Receiver"]
        [:th "Weight"]
        [:th "Box color"]
        [:th "Shipping cost"]]]
      [:tbody
       (for [{:box/keys [color country id name weight] :as box} (shared/denormalize boxes)]
         ^{:key id}
         [:tr {:id id}
          [:td name]
          [:td (str weight " kilograms")]
          (let [{:color/keys [r g]} color]
            [:td {:style {:background-color (color-picker/rgb-str r g)}}])
          [:td (str (shared/round-floor-to-2-deciamls
                     (* weight (country/multiplier country))) " Sek")]])]]
     [:div "Total weight: " (when boxes
                              (-> boxes
                                  (shared/denormalize)
                                  (boxinator/total-weight)))]
     [:div "Total cost: " (when boxes
                            (-> boxes
                                (shared/denormalize)
                                (boxinator/total-cost)
                                (shared/round-floor-to-2-deciamls)))]]))
