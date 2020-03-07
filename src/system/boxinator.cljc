(ns system.boxinator
  (:require
   [clojure.spec.alpha :as s]
   [clojure.spec.test.alpha :as stest]
   [clojure.string :as str]
   [clojure.test.check.generators :as gen]
   [medley.core :as medley]))

(s/def ::non-blank-string (s/and string? (complement str/blank?)))
(s/def ::uuid (s/with-gen medley/uuid? (fn [] gen/uuid)))


(s/def ::g pos-int?)
(s/def ::r pos-int?)
(s/def :boxinator/color (s/keys :req-un [::g ::r]))
(s/def :boxinator/country #{"Austalia" "Brazil" "China" "Sweden"})
(s/def :boxinator/name string?)
(s/def :boxinator/weight pos-int?)
(s/def :boxinator/box
  (s/keys :req [:boxinator/color
                :boxinator/country
                :boxinator/name
                :boxinator/weight]))


(s/def :country/id ::uuid)
(s/def :country/multiplier number?)
(s/def :country/name ::non-blank-string)
(s/def :boxinator/country
  (s/keys :req [:country/id
                :country/multiplier
                :country/name]))
(s/def :boxinator/countries
  (s/with-gen
    (s/and
     (s/map-of :country/id :boxinator/country)
     (s/every (fn [[k v]](= (:country/id v) k))))
    #(gen/fmap (fn [c]
                 (apply hash-map (->> c
                                      (map (juxt :country/id identity))
                                      (flatten))))
               (s/gen (s/coll-of :boxinator/country)))))


(defn denormalize-countries
  "Denormalize a countries map into a vector."
  [countries]
  (vec (vals countries)))


(s/fdef denormalize-countries
  :args (s/cat :m :boxinator/countries)
  :ret vector?
  :fn (fn [{:keys [args ret]}]
        (s/valid? (s/coll-of :boxinator/country) ret)))


(defn normalize-countries
  "Normalize a countries vector into a map."
  [countries]
  (->> countries
       (map (fn [{:country/keys [id] :as country}]
              {id country}))
       (into {})))


(s/fdef normalize-countries
  :args (s/cat :xs (s/coll-of :boxinator/country))
  :ret :boxinator/countries)


(comment
  ;; org.clojure/test.check {:mvn/version "1.0.0"}
  (stest/check `denormalize-countries)
  (stest/check `normalize-countries)

  ;; single
  (gen/generate (s/gen :boxinator/countries))
  ;; multi
  (gen/sample (s/gen :boxinator/countries))
  )
