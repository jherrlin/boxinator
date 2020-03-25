(ns system.boxinator
  "This namespace contains specs and functions tightly related to boxinator enteties."
  (:require
   [clojure.spec.alpha :as s]
   [clojure.test.check.generators :as gen]
   [system.country :as country]
   [system.shared :as shared]))


(s/def :color/g (s/int-in 0 255))
(s/def :color/r (s/int-in 0 255))
(s/def :box/color (s/keys :req [:color/g :color/r]))
(s/def :box/country #{#uuid "958e0376-eb26-428a-8147-7efc04e8d3e5"
                      #uuid "837225a9-f74d-447e-87bc-49c0b58ec972"
                      #uuid "b1ace9ef-c1fa-4c00-94fc-97db4618c245"
                      #uuid "8b759afd-1e0e-40ef-aecb-a3e48db4056e"})
(s/def :box/id ::shared/uuid)
(s/def :box/name ::shared/non-blank-string)
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


(defn normalize-boxes
  "Normalize a countries vector into a map."
  [boxes]
  (shared/normalize :box/id boxes))

(s/fdef normalize-boxes
  :args (s/cat :xs (s/coll-of :boxinator/box))
  :ret :boxinator/boxes)

(defn total-weight
  "Calculate the total weight of the boxes."
  [boxes-coll]
  {:pre [(s/valid? (s/coll-of :boxinator/box) boxes-coll)]}
  (->> boxes-coll
       (reduce
        (fn [total-weight {:box/keys [weight]}]
          (+ total-weight weight))
        0)))

(defn total-cost
  "Calculate the total cost of the boxes."
  [boxes-coll]
  {:pre [(s/valid? (s/coll-of :boxinator/box) boxes-coll)]}
  (->> boxes-coll
       (reduce
        (fn [i {:box/keys [country weight]}]
          (+ i
             (* weight (country/multiplier country))))
        0)))

(defn assoc-boxes-attributes
  "Assoc data to boxes before it goes to view.
  The attributes that are assoced on are: `:background-color` and `:shipping-cost`. This
  keys are not namespaced. This tells that they are not part of the `box` entity."
  [boxes]
  (->> boxes
       (mapv
        (fn [{:box/keys [color country weight] :as box}]
          (let [{:color/keys [r g]} color]
            (assoc box
                   :background-color (shared/rgb-str r g)
                   :shipping-cost (shared/round-floor-to-2-deciamls
                                   (* weight (country/multiplier country)))))))))

(defn assoc-box-form
  "Populare the db with a `:boxinator/box` form containing default values."
  [db]
  (assoc-in db [:form :boxinator/box :values :box/id] (shared/id)))


(comment
  ;; single
  (gen/generate (s/gen :boxinator/box))
  (gen/sample (s/gen :boxinator/box))
  )
