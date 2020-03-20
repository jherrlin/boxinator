(ns client.core
  (:refer-clojure :exclude [name])
  (:require
   [client.events :as events]
   [client.routes :as routes]
   [client.view-a :as view-a]
   [client.view-b :as view-b]
   [cljs.pprint :as pprint]
   [re-com.core :as re-com]
   [re-frame.core :as rf]
   [re-frame.db :as rf-db]
   [reagent.core :as reagent]))


(defn app-db-pre []
  [:div {:style {:width "100%" :display "flex" :justify-content "flex-end"}}
   [:pre (with-out-str (pprint/pprint @rf-db/app-db))]])

(defn panels [panel-name]
  (case panel-name
    :form  [view-a/form]
    :table [view-b/table]
    :main  [re-com/h-box
            :height "100%"
            :width "100%"
            :class "container"
            :children [[view-a/form]
                       [view-b/table]
                       [app-db-pre]]]
    [:div "something when wrong..."]))

(defn show-panel [panel-name]
  [panels panel-name])

(defn main-panel []
  (let [active-panel (rf/subscribe [:active-panel])]
    [show-panel @active-panel]))

(defn ^:dev/after-load mount-root []
  (rf/clear-subscription-cache!)
  (reagent/render [main-panel]
                  (.getElementById js/document "app")))

(defn init []
  (routes/app-routes)
  (rf/dispatch-sync [::events/initialize-db])
  (mount-root))
