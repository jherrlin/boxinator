(ns server.box.controls
  (:require
   [clojure.spec.alpha :as s]
   [server.box.db :as db]
   [ring.util.response :as response]
   [server.responses :as responses]))


(defn create
  [{:box/keys [id] :as box}]
  {:pre [(s/valid? :boxinator/box box)]}
  (db/save-box box)
  (responses/edn-response response/created (db/get-box id)))

(defn read
  ([]
   (responses/edn-response (db/get-boxes)))
  ([id]
   {:pre [(s/valid? :box/id id)]}
   (responses/edn-response (db/get-box id))))

(defn update
  [{:box/keys [id] :as box}]
  {:pre [(s/valid? :boxinator/box box)]}
  (db/save-box box)
  (responses/edn-response (db/get-box id)))

(defn delete
  [id]
  {:pre [(s/valid? :box/id id)]}
  (db/delete-box id)
  (responses/edn-response :deleted))
