(ns system.boxinator
  (:require
   [clojure.spec.alpha :as s]
   [clojure.spec.test.alpha :as stest]
   [clojure.string :as str]
   [clojure.test :as t]
   [clojure.test.check.generators :as gen]
   [medley.core :as medley]))

(s/def ::non-blank-string (s/and string? (complement str/blank?)))
(s/def ::uuid (s/with-gen medley/uuid? (fn [] gen/uuid)))


(s/def :color/g pos-int?)
(s/def :color/r pos-int?)
(s/def :box/color (s/keys :req [:color/g :color/r]))
(s/def :box/country ::uuid)
(s/def :box/id ::uuid)
(s/def :box/name string?)
(s/def :box/weight pos-int?)
(s/def :boxinator/box
  (s/keys :req [:box/color
                :box/country
                :box/id
                :box/name
                :box/weight]))
(s/def :boxinator/boxes
  (s/with-gen
    (s/and
     (s/map-of :box/id :boxinator/box)
     (s/every (fn [[k v]](= (:box/id v) k))))
    #(gen/fmap (fn [c]
                 (apply hash-map (->> c
                                      (map (juxt :box/id identity))
                                      (flatten))))
               (s/gen (s/coll-of :boxinator/box)))))


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


(defn denormalize
  "Denormalize a countries map into a vector."
  [countries]
  (vec (vals countries)))


(s/fdef denormalize
  :args (s/cat :m :boxinator/countries)
  :ret vector?
  :fn (fn [{:keys [args ret]}]
        (s/valid? (s/coll-of :boxinator/country) ret)))


(defn normalize [k xs]
  (when k
    (->> xs
         (map (fn [{id k :as x}]
                {id x}))
         (into {}))))


(t/deftest test-normalize
  (t/are [args-vector expected]
      (= (apply normalize args-vector) expected)
    [:a/id [{:a/id 1}
            {:a/id 2}]]
    {1 {:a/id 1}
     2 {:a/id 2}}
    [:a/id [{:a/id 1}]] {1 {:a/id 1}}
    [:a/id {}]          {}
    [nil nil]           nil
    [nil {}]            nil
    [:a/id nil]         {}))


(defn normalize-countries
  "Normalize a countries vector into a map."
  [countries]
  (normalize :country/id countries))


(s/fdef normalize-countries
  :args (s/cat :xs (s/coll-of :boxinator/country))
  :ret :boxinator/countries)


(defn normalize-boxes
  "Normalize a countries vector into a map."
  [boxes]
  (normalize :box/id boxes))


(s/fdef normalize-boxes
  :args (s/cat :xs (s/coll-of :boxinator/box))
  :ret :boxinator/boxes)


(comment
  (t/run-tests)

  ;; org.clojure/test.check {:mvn/version "1.0.0"}
  (stest/check `denormalize)
  (stest/check `normalize-countries)
  (stest/check `normalize-boxes)

  ;; single
  (gen/generate (s/gen :boxinator/countries))
  ;; multi
  (gen/sample (s/gen :boxinator/countries))

  (gen/generate (s/gen :boxinator/boxes))
  )
