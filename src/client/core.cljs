(ns client.core
  (:refer-clojure :exclude [name])
  (:require
   [client.color-picker :as color-picker]
   [medley.core :as medley]
   [client.events :as events]
   [client.forms :as forms]
   [client.inputs :as inputs]
   [clojure.spec.alpha :as s]
   [clojure.string :as str]
   [clojure.test.check.generators :as gen]
   [re-com.core :as re-com]
   [re-frame.core :as re-frame]
   [reagent.core :as reagent]
   [taoensso.sente :as sente]))


(s/def ::non-blank-string (s/and string? (complement str/blank?)))


(comment
  [:pre (with-out-str (cljs.pprint/pprint @re-frame.db/app-db))]
  )


(re-frame/reg-sub
 ::save?
 (fn [db _]
   (get db ::save?)))

(re-frame/reg-event-db
 ::save?
 (fn [db [_ b]]
   (assoc db ::save? b)))


(defn view-a-name [save?]
  (let [name          @(re-frame/subscribe [::events/name])
        name-visited? @(re-frame/subscribe [::events/name-visited?])]
    [forms/text
     {:label            "Name"
      :id               "view-a-name"
      :placeholder      "Name"
      :value            name
      :valid?           (s/valid? ::non-blank-string name)
      :on-change        #(re-frame/dispatch [::events/name %])
      :required?        true
      :show-validation? save?
      :focus?           true
      :visited?         true ;; As `focus?` is true, its logic to have this true
      :on-focus         #(re-frame/dispatch [::events/name-visited? true])
      :error-text       "A name needs to be provided."}]))


(s/def ::weight pos-int?)
(defn view-a-weight [save?]
  (let [weight          @(re-frame/subscribe [::events/weight])
        weight-visited? @(re-frame/subscribe [::events/weight-visited?])]
    [forms/number
     {:label            "Weight"
      :id               "view-a-weight"
      :placeholder      "Weight"
      :value            (str (or weight "0"))
      :valid?           (s/valid? ::weight weight)
      :on-change        #(re-frame/dispatch [::events/weight (js/parseInt %)])
      :on-blur          #(when (or (> 0 weight)
                                   (js/Number.isNaN weight))
                           (re-frame/dispatch [::events/weight 0]))
      :required?        true
      :show-validation? save?
      :visited?         true
      :on-focus         #(re-frame/dispatch [::events/weight-visited? true])
      :error-text       "Needs to be a positive number."}]))


(defn view-a-box-colour [save?]
  (let [{:keys [r g] :as color} @(re-frame/subscribe [::color-picker/selected-color])]
    [forms/text
     {:label            "Box colour"
      :id               "view-a-box-colour"
      :placeholder      "Click to show colour picker."
      :value            (when color (str r "," g "," 0))
      :show-validation? save?
      :required?        true
      :on-change        #()  ;; no-op
      :valid?           (s/valid? map? color)
      :visited?         true ;; As `focus?` is true, its logic to have this true
      :error-text       "Select a color"
      :on-focus         #(re-frame/dispatch [::color-picker/show-picker? true])}]))


(s/def ::id (s/with-gen medley/uuid? (fn [] gen/uuid)))
(s/def ::name ::non-blank-string)
(s/def ::country (s/keys :req-un [::id ::name]))
(defn view-a-countries [save?]
  (let [countries                @(re-frame/subscribe [:countries])
        {:keys [id] :as country} @(re-frame/subscribe [::events/country])]
    [forms/select
     {:id               "view-a-countries"
      :label            "Country"
      :on-select        #(re-frame/dispatch [::events/country %])
      :selected-id      id
      :choices          countries
      :required?        true
      :show-validation? save?
      :valid?           (s/valid? ::country country)
      :on-focus         #(re-frame/dispatch [::events/country-visited? true])
      :visited?         true
      :error-text       "Select a country."}]))


(defn view-a []
  (let [save? @(re-frame/subscribe [::save?])]
    [:div {:style {:min-width "300px"}}
     [view-a-name save?]
     [view-a-weight save?]
     [view-a-box-colour save?]
     [color-picker/ui]
     [view-a-countries save?]
     [:div {:style {:display "flex"
                    :justify-content "flex-end"}}
      [:button.btn.btn-default
       {:on-click #(re-frame/dispatch [::save? true])}
       "Save"]]]))


(defn app-init []
  [re-com/h-box
   :height "100%"
   :width "100%"
   :class "container"
   :children [[view-a]
              [:div {:style {:width "100%" :display "flex" :justify-content "flex-end"}}
               [:pre (with-out-str (cljs.pprint/pprint @re-frame.db/app-db))]]]])


(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [app-init]
                  (.getElementById js/document "app")))


(defn init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (mount-root))
