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

   {:n :active-panel}
   {:n :form/save?}])


(doseq [{:keys [n s e]} events-]
  (re-frame/reg-sub n (or s (fn [db _] (n db))))
  (re-frame/reg-event-db n (or e (fn [db [_ e]] (assoc db n e)))))


(re-frame/reg-sub
 :res
 (fn [db [k]]
   (get-in db [:res])))


(re-frame/reg-sub
 ::form
 (fn [db [k form]]
   (get-in db [:form form :values])))


(re-frame/reg-event-db
 ::form
 (fn [db [_ form value]]
   (assoc-in db [:form form] value)))


(re-frame/reg-event-db
 ::form-value
 (fn [db [_ form attr value]]
   (assoc-in db [:form form :values attr] value)))


(re-frame/reg-sub
 ::form-value
 (fn [db [k form attr]]
   (get-in db [:form form :values attr])))


(re-frame/reg-event-db
 ::form-meta
 (fn [db [_ form attr meta]]
   (assoc-in db [:form form :meta attr] meta)))


(re-frame/reg-sub
 ::form-meta
 (fn [db [k form attr]]
   (get-in db [:form form :meta attr])))


(re-frame/reg-event-db
 ::success-post-result
 (fn [db [_ res]]
   (assoc db :spinner? false :res res)))


(re-frame/reg-event-db
 ::save-form-success
 (fn [db [_ form results]]
   (-> db
       (assoc-in [:form form] {})
       (assoc    :res results))))


(re-frame/reg-event-db
 ::save-form-failure
 (fn [db [_ form results]]
   (-> db
       (assoc-in [:form form :meta :waiting?] false)
       (assoc    :res results))))


(re-frame/reg-event-fx
 ::save-form
 (fn [{:keys [db]} [k form]]
   (let [form-data (get-in db [:form form :values])]
     {:db (assoc-in db [:form form :meta :waiting?] true)
      :http-xhrio {:method          :post
                   :uri             "http://localhost:8080/boxes"
                   :params          form-data
                   :timeout         5000
                   :format          (ajax.edn/edn-request-format)
                   :response-format (ajax.edn/edn-response-format)
                   :on-success      [::save-form-success form]
                   :on-failure      [::save-form-failure form]}})))


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
               :frequency 20000
               :event     [::get]}}))


(re-frame/reg-event-fx
 :stop-poll
 (fn [{:keys [db] :as cofx} [k]]
   {:interval {:action    :stop
               :id        :get-query}}))


(re-frame/reg-event-fx
 :route/addbox
 (fn [{:keys [db] :as cofx} [k]]
   {:db       (-> db
                  (assoc :active-panel :form)
                  (assoc-in [:form :boxinator/box :box/id] (medley/random-uuid)))
    :interval {:action :stop
               :id     :get-query}}))


(re-frame/reg-event-fx
 :route/listboxes
 (fn [{:keys [db] :as cofx} [k]]
   {:db         (assoc db :active-panel :table)
    :http-xhrio {:method          :get
                 :uri             "http://localhost:8080/boxes"
                 :timeout         5000
                 :response-format (ajax.edn/edn-response-format)
                 :on-success      [::success-post-result]
                 :on-failure      [::http-failure]}
    :interval   {:action    :start
                 :id        :get-query
                 :frequency 20000
                 :event     [::get]}}))


(re-frame/reg-event-fx
 :route/main
 (fn [{:keys [db] :as cofx} [k]]
   {:db         (assoc db :active-panel :main)
    :http-xhrio {:method          :get
                 :uri             "http://localhost:8080/boxes"
                 :timeout         5000
                 :response-format (ajax.edn/edn-response-format)
                 :on-success      [::success-post-result]
                 :on-failure      [::http-failure]}
    :interval   {:action    :start
                 :id        :get-query
                 :frequency 20000
                 :event     [::get]}}))


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
