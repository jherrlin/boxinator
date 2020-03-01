(ns client.inputs
  (:require
   [clojure.string :as str]
   [clojure.spec.alpha :as s]
   [medley.core :as medley]
   [clojure.test.check.generators :as gen]))


(defn text [{:keys [id value on-change placeholder on-mouse-up on-blur]
             :or {placeholder ""
                  on-change #(js/console.log "text: "  (.. % -target -value))
                  on-mouse-up (fn [] (js/console.log "on-mouse-up"))
                  on-blur (fn [] (js/console.log "on-blur"))}
             :as props}]
  {:pre [(string? id)]}
  [:input.form-control
   {:id id
    :on-change #(on-change (.. % -target -value))
    :on-mouse-up on-mouse-up
    :on-blur on-blur
    :type "text"
    :placeholder placeholder
    :value (or value "")}])


(defn number [{:keys [id value on-change placeholder on-mouse-up on-blur]
               :or {placeholder ""
                    on-change #(js/console.log "numer: "  (.. % -target -value))
                    on-mouse-up (fn [] (js/console.log "on-mouse-up"))
                    on-blur (fn [] (js/console.log "on-blur"))}
               :as props}]
  {:pre [(string? id)]}
  [:input.form-control
   {:id id
    :on-change #(on-change (.. % -target -value))
    :on-mouse-up on-mouse-up
    :on-blur on-blur
    :type "number"
    :placeholder placeholder
    :value value}])


(s/def ::non-blank-string (s/and string? (complement str/blank?)))
(s/def ::id (s/with-gen medley/uuid? (fn [] gen/uuid)))
(s/def ::name ::non-blank-string)
(s/def ::choice (s/keys :req-un [::id ::name]))
(s/def ::choices
  (s/with-gen
    (s/and
     (s/map-of ::id ::choice)
     (s/every (fn [[k v]] (= k (:id v)))))
    #(gen/fmap (fn [c]
                 (apply hash-map (->> c
                                      (map (juxt :id identity))
                                      (flatten))))
               (s/gen (s/coll-of ::choice)))))


(defn select [{:keys [id choices on-select]
               :or {on-select #(js/console.log "no `on-select` fn. But selected:" %)}
               :as props}]
  {:pre [(s/valid? ::choices choices)]}
  [:select {:id        id
            :class     "form-control"
            :on-change #(let [choice-id (-> % (.-target) (.-value) (medley/uuid))]
                          (on-select (get choices choice-id)))}
   (for [{:keys [id name]} (vals choices)]
     ^{:key (str "select-value-" id)}
     [:option {:value id}
      name])])



(comment
  (gen/generate (s/gen ::choices))  ;; Single
  (gen/sample (s/gen ::choices))    ;; Multi
  (s/valid? ::choices {})
  )
