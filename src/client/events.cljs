(ns client.events
  (:require
   [day8.re-frame.http-fx]
   [client.events.routes]
   [ajax.core :as ajax]
   [ajax.formats]
   [ajax.edn]
   [re-frame.core :as re-frame]))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   {}))


(def events-
  [{:n :active-panel}
   {:n :boxes}
   {:n :form/save?}])


(doseq [{:keys [n s e]} events-]
  (re-frame/reg-sub n (or s (fn [db _] (n db))))
  (re-frame/reg-event-db n (or e (fn [db [_ e]] (assoc db n e)))))


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
                   :on-success      [:request/success]
                   :on-failure      [:request/failed]}})))
