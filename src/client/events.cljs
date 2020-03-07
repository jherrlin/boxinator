(ns client.events
  (:require
   [client.db :as db]
   [day8.re-frame.http-fx]
   [ajax.core :as ajax]
   [clojure.edn :as edn]
   [ajax.formats]
   [ajax.edn]
   [medley.core :as medley]
   [re-frame.core :as re-frame]))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/app-db))


(def events-
  [{:n ::name}
   {:n ::country}
   {:n ::weight}
   {:n ::rgb}

   {:n :countries}])


(doseq [{:keys [n s e]} events-]
  (re-frame/reg-sub n (or s (fn [db _] (n db))))
  (re-frame/reg-event-db n (or e (fn [db [_ e]] (assoc db n e)))))


(re-frame/reg-event-db
 ::success-post-result
 (fn [db [_ res]]
   (assoc db :spinner? false :res res)))


(re-frame/reg-event-db
 ::failure-post-result
 (fn [db [_ res]]
   (assoc db :spinner? false :http-error :true)))


(re-frame/reg-event-fx
 ::save
 (fn [{:keys [db]} [_ data]]
   {:db         (assoc db :spinner? true)
    :http-xhrio {:method          :post
                 :uri             "http://localhost:8080/boxes"
                 :params          data
                 :timeout         5000
                 :format          (ajax.edn/edn-request-format)
                 :response-format (ajax.edn/edn-response-format)
                 :on-success      [::success-post-result]
                 :on-failure      [::http-failure]}}))


(re-frame/reg-fx
 :interval
 (let [live-intervals (atom {})]
   (fn [{:keys [action id frequency event] :as m}]
     (println "in here")
     (js/console.log "john-debug: YO!" m)
     (if (= action :start)
       (swap! live-intervals assoc id (js/setInterval #(re-frame/dispatch event) frequency))
       (do (js/clearInterval (get @live-intervals id))
           (swap! live-intervals dissoc id))))))


(re-frame/reg-event-fx
 ::get
 (fn [{:keys [db]} [_]]
   {:db         (assoc db :spinner? true)
    :http-xhrio {:method          :get
                 :uri             "http://localhost:8080/boxes"
                 :timeout         5000
                 :response-format (ajax.edn/edn-response-format)
                 :on-success      [::success-post-result]
                 :on-failure      [::http-failure]}}))


(re-frame/reg-event-fx
 :start-poll
 (fn [{:keys [db] :as cofx} [k]]
   {:interval {:action    :start
               :id        :get-query
               :frequency 5000
               :event     [::get]}}))


(re-frame/reg-event-fx
 :stop-poll
 (fn [{:keys [db] :as cofx} [k]]
   {:interval {:action    :stop
               :id        :get-query}}))







(comment
  (do
    (re-frame/dispatch [::get])
    (re-frame/dispatch [:start-poll]))

  (re-frame/dispatch [:stop-poll])


  (re-frame/dispatch [::save {:box/id      (medley/random-uuid)
                              :box/name    "JOHN"
                              :box/weight  777
                              :box/color   "1,2,3"
                              :box/country (medley/random-uuid)}])
  )
