(ns system.shared
  "This namespace contains functions and specs that are used by other shared resources. This
  namespace should not contain any dependencies to other namespaces in the project."
  (:require
   [clojure.spec.alpha :as s]
   [clojure.string :as str]
   [clojure.test.check.generators :as gen]
   [medley.core :as medley]))


(s/def ::non-blank-string (s/and string? (complement str/blank?)))
(s/def ::uuid (s/with-gen medley/uuid? (fn [] gen/uuid)))


(defn denormalize
  "Denormalize a countries map into a vector."
  [countries]
  (vec (vals countries)))

(s/fdef denormalize
  :args (s/cat :m :boxinator/countries)
  :ret vector?
  :fn (fn [{:keys [args ret]}]
        (s/valid? (s/coll-of :boxinator/country) ret)))

(defn normalize
  "Normalize a list of enteties into a map with a `{id entity ...}`."
  [k xs]
  (when k
    (->> xs
         (map (fn [{id k :as x}]
                {id x}))
         (into {}))))
