(ns server.db
  (:require
   [clojure.spec.alpha :as s]
   [clojure.test.check.generators :as gen]
   [datomic.api :as d]
   [system.boxinator :as boxinator]))

;; Create an in memory datomic database.
(let [db-name (gensym)
      db-uri (str "datomic:mem://" db-name)]
  (d/create-database db-uri)
  (let [conn (d/connect db-uri)]
    (def conn conn)))

(d/transact
 conn
 ;; This is the datomic database schema
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

(defn get-boxes
  "Get boxes from database."
  []
  (let [db (d/db conn)]
    (->> (d/q '[:find [(pull ?e [{:box/color [:color/g
                                              :color/r]}
                                 :box/country
                                 :box/id
                                 :box/name
                                 :box/weight])
                       ...]
                :where [?e :box/id]]
              db)
         (boxinator/normalize-boxes))))

(defn save-box
  "Save box to database if `m` is a valid `box` entity. Else throw."
  [m]
  {:pre [(s/valid? :boxinator/box m)]}
  (d/transact conn [m]))


(comment
  (save-box (gen/generate (s/gen :boxinator/box)))
  (get-boxes)
  )
