(ns system.country
  (:require
   [clojure.spec.alpha :as s]
   [system.shared :as shared]
   [clojure.test.check.generators :as gen]))


(s/def :country/id ::shared/uuid)
(s/def :country/multiplier number?)
(s/def :country/name ::shared/non-blank-string)
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

(defn normalize-countries
  "Normalize a countries vector into a map."
  [countries]
  (shared/normalize :country/id countries))

(s/fdef normalize-countries
  :args (s/cat :xs (s/coll-of :boxinator/country))
  :ret :boxinator/countries)

(def countries
  {#uuid "958e0376-eb26-428a-8147-7efc04e8d3e5"
   #:country{:id #uuid "958e0376-eb26-428a-8147-7efc04e8d3e5",
             :multiplier 1.3,
             :name "Sweden"},
   #uuid "837225a9-f74d-447e-87bc-49c0b58ec972"
   #:country{:id #uuid "837225a9-f74d-447e-87bc-49c0b58ec972",
             :multiplier 4,
             :name "China"},
   #uuid "b1ace9ef-c1fa-4c00-94fc-97db4618c245"
   #:country{:id #uuid "b1ace9ef-c1fa-4c00-94fc-97db4618c245",
             :multiplier 8.6
             :name "Brazil"},
   #uuid "8b759afd-1e0e-40ef-aecb-a3e48db4056e"
   #:country{:id #uuid "8b759afd-1e0e-40ef-aecb-a3e48db4056e",
             :multiplier 7.2
             :name "Australia"}})

(defn country
  "Return country map from id."
  [id]
  {:pre [(s/valid? :country/id id)]}
  (get countries id))

(defn multiplier
  "Return the multiplier related to a country"
  [id]
  {:pre [(s/valid? :country/id id)]}
  (-> (country id) :country/multiplier))


(comment
  ;; generate single
  (gen/generate (s/gen :boxinator/country))

  ;; validate
  (s/valid? :boxinator/countries countries)
  )
