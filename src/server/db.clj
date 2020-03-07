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
 [{:db/ident       :boxinator/id
   :db/cardinality :db.cardinality/one
   :db/unique      :db.unique/identity
   :db/valueType   :db.type/uuid
   :db/doc         "Entity id"}

  {:db/ident       :boxinator/name
   :db/cardinality :db.cardinality/one
   :db/valueType   :db.type/string}

  {:db/ident       :boxinator/color
   :db/cardinality :db.cardinality/one
   :db/valueType   :db.type/string}

  {:db/ident       :boxinator/weight
   :db/cardinality :db.cardinality/one
   :db/valueType   :db.type/long}

  {:db/ident       :boxinator/country
   :db/cardinality :db.cardinality/one
   :db/valueType   :db.type/uuid}])


(defn get-boxes []
  (let [db (d/db conn)]
    (d/q '[:find [(pull ?e [:boxinator/id
                            :boxinator/name
                            :boxinator/weight
                            :boxinator/color
                            :boxinator/country])
                  ...]
           :where [?e :boxinator/id]]
         db)))


(defn save-box [m]
  (d/transact conn [m]))


(comment
  (save-box
   {:boxinator/id (medley/random-uuid)
    :boxinator/name "John!"
    :boxinator/weight 666
    :boxinator/color "1,2,3"
    :boxinator/country (medley/random-uuid)})

  (get-boxes)

  (medley/uuid "6051a16f-6046-4415-bafa-e067789d1863")
  )
