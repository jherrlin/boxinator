(ns client.core
  (:require
   [re-com.core :as re-com]
   [re-frame.core :as re-frame]
   [reagent.core :as reagent]
   [taoensso.sente :as sente]))


(defn app-init []
  [re-com/box
   :height "100%"
   :max-height "100%"
   :width "100%"
   :class "container"
   :child [:div "New app using re-frame, re-com!"]])


(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [app-init]
                  (.getElementById js/document "app")))


(defn init []
  (mount-root))
