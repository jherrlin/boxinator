(ns client.routes
  (:require-macros [secretary.core :refer [defroute]])
  (:import [goog History]
           [goog.history EventType])
  (:require
   [secretary.core :as secretary]
   [goog.events :as gevents]
   [re-frame.core :as re-frame]))


(defn hook-browser-navigation! []
  (doto (History.)
    (gevents/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn app-routes []
  (secretary/set-config! :prefix "#")
  (defroute "/addbox" []
    (re-frame/dispatch [:route/addbox]))

  (defroute "/listboxes" []
    (re-frame/dispatch [:route/listboxes]))

  (defroute "/" []
    (re-frame/dispatch [:route/main]))

  (hook-browser-navigation!))
