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

  {:db/ident       :box/color
   :db/cardinality :db.cardinality/one
   :db/valueType   :db.type/string}

  {:db/ident       :box/weight
   :db/cardinality :db.cardinality/one
   :db/valueType   :db.type/long}

  {:db/ident       :box/country
   :db/cardinality :db.cardinality/one
   :db/valueType   :db.type/uuid}])


(defn get-boxes []
  (let [db (d/db conn)]
    (d/q '[:find [(pull ?e [:box/id
                            :box/name
                            :box/weight
                            :box/color
                            :box/country])
                  ...]
           :where [?e :box/id]]
         db)))


(defn save-box [m]
  (d/transact conn [m]))


(comment
  (save-box
   {:box/id (medley/random-uuid)
    :box/name "John!"
    :box/weight 666
    :box/color "1,2,3"
    :box/country (medley/random-uuid)})

  (get-boxes)

  (medley/uuid "6051a16f-6046-4415-bafa-e067789d1863")
  )
