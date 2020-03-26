(ns client.events.routes
  (:require
   [ajax.core :as ajax]
   [ajax.edn]
   [ajax.formats]
   [day8.re-frame.http-fx]
   [re-frame.core :as re-frame]
   [system.boxinator :as boxinator]
   [system.shared :as shared]))


(re-frame/reg-event-db
 :request/success
 (fn [db [_ results-from-server]]
   (assoc db :boxes results-from-server)))


(re-frame/reg-event-db
 :request/form-success
 (fn [db [_ form results-from-server]]
   (-> db
       (assoc :boxes results-from-server)
       (assoc-in [:form form] {})
       (boxinator/assoc-box-form))))


(re-frame/reg-event-db
 :request/failed
 (fn [db [_]]
   (assoc db :boxes {})))


(re-frame/reg-fx
 :interval
 (let [live-intervals (atom {})]
   (fn [{:keys [action id frequency event] :as m}]
     (if (= action :start)
       (swap! live-intervals assoc id (js/setInterval #(re-frame/dispatch event) frequency))
       (do (js/clearInterval (get @live-intervals id))
           (swap! live-intervals dissoc id))))))


(re-frame/reg-event-fx
 :request/get
 (fn [{:keys [db]} [_]]
   {:http-xhrio {:method          :get
                 :uri             "http://localhost:8080/api/boxes"
                 :timeout         5000
                 :response-format (ajax.edn/edn-response-format)
                 :on-success      [:request/success]
                 :on-failure      [:request/failed]}}))


(re-frame/reg-event-fx
 :start-poll
 (fn [{:keys [db] :as cofx} [k]]
   {:interval {:action    :start
               :id        :get-query
               :frequency 20000
               :event     [:request/get]}}))


(re-frame/reg-event-fx
 :stop-poll
 (fn [{:keys [db] :as cofx} [k]]
   {:interval {:action :stop
               :id     :get-query}}))


(re-frame/reg-event-fx
 :route/addbox
 (fn [{:keys [db] :as cofx} [k]]
   {:db       (-> db
                  (assoc :active-panel :form)
                  (boxinator/assoc-box-form))
    :interval {:action :stop
               :id     :get-query}}))


(re-frame/reg-event-fx
 :route/listboxes
 (fn [{:keys [db] :as cofx} [k]]
   {:db         (assoc db :active-panel :table)
    :http-xhrio {:method          :get
                 :uri             "http://localhost:8080/api/boxes"
                 :timeout         5000
                 :response-format (ajax.edn/edn-response-format)
                 :on-success      [:request/success]
                 :on-failure      [:request/failed]}
    :interval   {:action    :start
                 :id        :get-query
                 :frequency 20000
                 :event     [:request/get]}}))


(re-frame/reg-event-fx
 :route/main
 (fn [{:keys [db] :as cofx} [k]]
   {:db         (-> db
                    (assoc :active-panel :main)
                    (boxinator/assoc-box-form))
    :http-xhrio {:method          :get
                 :uri             "http://localhost:8080/api/boxes"
                 :timeout         5000
                 :response-format (ajax.edn/edn-response-format)
                 :on-success      [:request/success]
                 :on-failure      [:request/failed]}
    :interval   {:action    :start
                 :id        :get-query
                 :frequency 20000
                 :event     [:request/get]}}))
