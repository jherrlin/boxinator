(ns server.box.db
  (:require
   [clojure.spec.alpha :as s]
   [server.db :as db]
   [clojure.test.check.generators :as gen]
   [datomic.api :as d]
   [system.boxinator :as boxinator]))

(def schema
  [{:db/ident       :box/id
    :db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity
    :db/valueType   :db.type/uuid
    :db/doc         "Entity id"}

   {:db/ident       :box/name
    :db/cardinality :db.cardinality/one
    :db/valueType   :db.type/string}

   {:db/ident       :color/r
    :db/cardinality :db.cardinality/one
    :db/valueType   :db.type/long}

   {:db/ident       :color/g
    :db/cardinality :db.cardinality/one
    :db/valueType   :db.type/long}

   {:db/ident       :color/b
    :db/cardinality :db.cardinality/one
    :db/valueType   :db.type/long}

   {:db/ident       :box/color
    :db/cardinality :db.cardinality/one
    :db/isComponent true
    :db/valueType   :db.type/ref}

   {:db/ident       :box/weight
    :db/cardinality :db.cardinality/one
    :db/valueType   :db.type/long}

   {:db/ident       :box/country
    :db/cardinality :db.cardinality/one
    :db/valueType   :db.type/uuid}])

(def box-pull [{:box/color [:color/g
                            :color/r]}
               :box/country
               :box/id
               :box/name
               :box/weight])

(defn get-box
  "Get box by id."
  [id]
  (let [db (d/db (db/conn))]
    (->> (d/q '[:find (pull ?e box-pull) .
                :in $ box-pull ?id
                :where [?e :box/id ?id]]
              db box-pull id))))

(defn get-boxes
  "Get boxes from database."
  []
  (let [db (d/db (db/conn))]
    (->> (d/q '[:find [(pull ?e box-pull) ...]
                :in $ box-pull
                :where [?e :box/id]]
              db box-pull)
         (boxinator/normalize-boxes))))

(defn save-box
  "Save box to database if `m` is a valid `box` entity. Else throw."
  [m]
  {:pre [(s/valid? :boxinator/box m)]}
  (d/transact (db/conn) [m]))

(defn delete-box
  [id]
  (let [db (d/db (db/conn))]
    (when-let [entity-id (d/q '[:find ?e .
                                :in $ ?id
                                :where [?e :box/id ?id]]
                              db id)]
      (d/transact
       (db/conn)
       [[:db/retractEntity entity-id]
        [:db/add "datomic.tx" :db/doc "Removed entity"]]))))




(comment
  (save-box (gen/generate (s/gen :boxinator/box)))
  (get-boxes)
  )
