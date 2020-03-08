(ns client.core
  (:refer-clojure :exclude [name])
  (:require
   [client.color-picker :as color-picker]
   [client.events :as events]
   [client.forms :as forms]
   [client.inputs :as inputs]
   [clojure.spec.alpha :as s]
   [clojure.string :as str]
   [clojure.test.check.generators :as gen]
   [medley.core :as medley]
   [re-com.core :as re-com]
   [re-frame.core :as rf]
   [reagent.core :as reagent]
   [taoensso.sente :as sente]))


(comment
  [:pre (with-out-str (cljs.pprint/pprint @re-frame.db/app-db))]

  (->> @re-frame.db/app-db
       :form
       :boxinator/box
       :values
       (s/valid? :boxinator/box)
       )
  )




(defn view-a-name [{:keys [save? form]}]
  (let [attr  :box/name
        value @(rf/subscribe [::events/form-value form attr])]
    [forms/text
     {:label       "Name"
      :id          "view-a-name"
      :placeholder "Name"
      :value       value
      :valid?      (s/valid? attr value)
      :on-change   #(rf/dispatch [::events/form-value form attr %])
      :required?   true
      :focus?      true
      :visited?    save?
      :error-text  "A name needs to be provided."}]))


(defn view-a-weight [{:keys [save? form]}]
  (let [attr   :box/weight
        weight @(rf/subscribe [::events/form-value form attr])]
    [forms/number
     {:label       "Weight"
      :id          "view-a-weight"
      :placeholder "Weight"
      :value       (str (or weight "0"))
      :valid?      (s/valid? attr weight)
      :on-change   #(rf/dispatch [::events/form-value form attr (js/parseInt %)])
      :on-blur     #(when (or (> 0 weight)
                              (js/Number.isNaN weight))
                      (rf/dispatch [::events/form-value form attr 0]))
      :required?   true
      :visited?    save?
      :error-text  "Needs to be a positive number."}]))


(defn view-a-box-colour [{:keys [save? form]}]
  (let [attr :box/color
        {:keys [r g] :as color} @(rf/subscribe [::events/form-value form attr])
        show-picker? @(rf/subscribe [::events/form-meta form :color-picker/show?])]
    [forms/color-picker
     {:label          "Box colour"
      :id             "view-a-box-colour"
      :placeholder    "Click to show colour picker."
      :value          (when color (str r "," g "," 0))
      :required?      true
      :on-change      #() ;; no-op on text input
      :valid?         (s/valid? attr color)
      :visited?       save?
      :error-text     "No color is selected"
      :show-picker?   show-picker?
      :color          color
      :on-focus       #(rf/dispatch [::events/form-meta  form :color-picker/show? true])
      :on-done        #(rf/dispatch [::events/form-meta  form :color-picker/show? false])
      :on-color-click #(rf/dispatch [::events/form-value form attr %])
      :attr           {:autoComplete "off"}}]))


(defn view-a-countries [{:keys [save? form]}]
  (let [attr :box/country
        countries @(rf/subscribe [:countries])
        value @(rf/subscribe [::events/form-value form attr])]
    [forms/select
     {:id          "view-a-countries"
      :label       "Country"
      :on-select   #(rf/dispatch [::events/form-value form attr (:country/id %)])
      :selected-id value
      :choices     countries
      :required?   true
      :valid?      (s/valid? :country/id value)
      :visited?    save?
      :error-text  "Select a country."}]))


(defn view-a []
  (let [form :boxinator/box]
    (reagent/create-class
     {:display-name "focus-when-empty"
      :component-did-mount
      #(rf/dispatch [::events/form-value form :box/id (medley/random-uuid)])
      :reagent-render
      (fn []
        (let [save? @(rf/subscribe [::events/form-meta form :form/save?])]
          [:div {:style {:min-width "300px"}}
           [view-a-name {:save? save? :form form}]
           [view-a-weight {:save? save? :form form}]
           [view-a-box-colour {:save? save? :form form}]
           [view-a-countries {:save? save? :form form}]
           [:div
            {:style {:display "flex" :justify-content "flex-end"}}
            [:button.btn.btn-default
             {:on-click
              #(rf/dispatch [::events/form-meta form :form/save? true])}
             "Save"]]]))})))


(defn app-init []
  [re-com/h-box
   :height "100%"
   :width "100%"
   :class "container"
   :children [[view-a]
              [:div {:style {:width "100%" :display "flex" :justify-content "flex-end"}}
               [:pre (with-out-str (cljs.pprint/pprint @re-frame.db/app-db))]]]])


(defn ^:dev/after-load mount-root []
  (rf/clear-subscription-cache!)
  (reagent/render [app-init]
                  (.getElementById js/document "app")))


(defn init []
  (rf/dispatch-sync [::events/initialize-db])
  (mount-root))
