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


(comment
  (re-frame/dispatch [::get])
  (re-frame/dispatch [::save {:boxinator/id (medley/random-uuid)
                              :boxinator/name "JOHN"
                              :boxinator/weight 777
                              :boxinator/color "1,2,3"
                              :boxinator/country (medley/random-uuid)}])
  )
