(ns server.db
  (:require
   [clojure.spec.alpha :as s]
   [clojure.test.check.generators :as gen]
   [datomic.api :as d]
   [system.boxinator :as boxinator]))

(def state (atom {:conn nil}))

(defn conn []
  (-> @state :conn))

(defn connect! []
  (let [db-name (gensym)
        db-uri (str "datomic:mem://" db-name)]
    (d/create-database db-uri)
    (let [conn (d/connect db-uri)]
      (d/transact conn schema)
      (swap! state assoc :conn conn))))

(defn disconnect! []
  (swap! state assoc :conn nil))
