(ns server.db
  (:require
   [datomic.api :as d]
   [medley.core :as medley]))


(let [db-name (gensym)
      db-uri (str "datomic:mem://" db-name)]
  (d/create-database db-uri)
  (let [conn (d/connect db-uri)]
    (def conn conn)))


(d/transact
 conn
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


(defn get-boxes []
  (let [db (d/db conn)]
    (d/q '[:find [(pull ?e [:box/color
                            :box/country
                            :box/id
                            :box/name
                            :box/weight])
                  ...]
           :where [?e :box/id]]
         db)))


(defn save-box [m]
  (d/transact conn [m]))


(comment
  (save-box
   {:box/id #uuid "449e3848-669d-4b2e-b1c4-acfb7663fffb",
    :box/name "JOhn!!!!",
    :box/weight 4,
    :box/color {:color/r 55, :color/g 175, :color/b 0},
    :box/country #uuid "8b759afd-1e0e-40ef-aecb-a3e48db4056e"})

  (get-boxes)
  )
