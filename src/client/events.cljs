(ns client.events
  (:require
   [client.db :as db]
   [re-frame.core :as re-frame]))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/app-db))


(def events-
  [{:n ::name}
   {:n ::name-visited?}
   {:n :countries}
   {:n ::country}
   {:n ::country-visited?}
   {:n ::weight}
   {:n ::weight-visited?}
   {:n ::rgb}
   {:n ::rgb-visited?}])


(doseq [{:keys [n s e]} events-]
  (re-frame/reg-sub n (or s (fn [db _] (n db))))
  (re-frame/reg-event-db n (or e (fn [db [_ e]] (assoc db n e)))))
