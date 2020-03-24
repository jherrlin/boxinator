(ns system.shared
  "This namespace contains functions and specs that are used by other shared resources. This
  namespace should not contain any dependencies to other namespaces in the project."
  (:refer-clojure :exclude [format])
  (:require
   #?(:cljs [goog.string :as gstring])
   #?(:cljs [goog.string.format])
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
         (map (juxt k identity))
         (into {}))))

(defn id
  "Generate id"
  []
  (medley/random-uuid))

(defn id?
  "If `x` is a valid id, return true. Else nil."
  [x]
  (medley/uuid? x))

(defn str->id
  "Convert string into id."
  [s]
  (when (string? s)
    (medley/uuid s)))

(defn format
  "General `format` for both clj and cljs."
  [exp s]
  #?(:clj  (clojure.core/format exp s)
     :cljs (gstring/format      exp s)))

(defn round-floor-to-2-deciamls
  "Format a number down to 2 decimals."
  [f]
  (->> (double f)
       (format "%.2f")))
