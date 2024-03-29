(ns client.view-a
  (:refer-clojure :exclude [name])
  (:require
   [client.events :as events]
   [client.form-inputs :as form-inputs]
   [client.inputs :as inputs]
   [clojure.spec.alpha :as s]
   [re-frame.core :as rf]
   [system.country :as country]))


(defn name [{:keys [form save?]}]
  (let [attr  :box/name
        value @(rf/subscribe [::events/form-value form attr])]
    [form-inputs/text
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

(defn weight [{:keys [form save?]}]
  (let [attr   :box/weight
        weight @(rf/subscribe [::events/form-value form attr])]
    [form-inputs/number
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

(defn box-colour [{:keys [form save?]}]
  (let [attr :box/color
        {:color/keys [g r] :as color} @(rf/subscribe [::events/form-value form attr])
        show-picker? @(rf/subscribe [::events/form-meta form :color-picker/show?])]
    [form-inputs/color-picker
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

(defn countries [{:keys [save? form]}]
  (let [attr :box/country
        value @(rf/subscribe [::events/form-value form attr])]
    [form-inputs/select
     {:id          "view-a-countries"
      :label       "Country"
      :on-select   #(rf/dispatch [::events/form-value form attr (:country/id %)])
      :selected-id value
      :choices     country/countries
      :required?   true
      :valid?      (s/valid? :country/id value)
      :visited?    save?
      :error-text  "Select a country."}]))

(defn save-validate-button [save? form form-valid?]
  [:div
   {:style {:display "flex" :justify-content "flex-end"}}
   [inputs/button
    {:id "validate-save-form-button"
     :disabled? (when save? (not form-valid?))
     :on-click #(do (rf/dispatch [::events/form-meta form :form/save? true])
                    (when (and save? form-valid?)
                      (rf/dispatch [::events/save-form form])))
     :body (if (and save? form-valid?)
             "Save" "Validate")}]])

(defn form []
  (let [form        :boxinator/box
        save?       @(rf/subscribe [::events/form-meta form :form/save?])
        form-valid? @(rf/subscribe [::events/form-valid? form])]
    [:div {:style {:min-width "300px"}}
     [name {:save? save? :form form}]
     [weight {:save? save? :form form}]
     [box-colour {:save? save? :form form}]
     [countries {:save? save? :form form}]
     [save-validate-button save? form form-valid?]]))
