(ns client.inputs
  (:require
   [system.boxinator :as boxinator]
   [clojure.spec.alpha :as s]
   [medley.core :as medley]))


(defn text [{:keys [attr id on-blur on-change on-focus placeholder value]
             :or {placeholder ""
                  on-change #(js/console.log "text: "  (.. % -target -value))
                  on-focus (fn [] (js/console.log "on-focus"))
                  on-blur (fn [] (js/console.log "on-blur"))}
             :as props}]
  {:pre [(string? id)]}
  [:input.form-control
   (merge
    {:id id
     :on-change #(on-change (.. % -target -value))
     :on-focus on-focus
     :on-blur on-blur
     :type "text"
     :placeholder placeholder
     :value (or value "")}
    attr)])


(defn number [{:keys [attr id on-blur on-change on-focus placeholder value]
               :or {placeholder ""
                    on-change #(js/console.log "numer: "  (.. % -target -value))
                    on-focus (fn [] (js/console.log "on-focus"))
                    on-blur (fn [] (js/console.log "on-blur"))}
               :as props}]
  {:pre [(string? id)]}
  [:input.form-control
   (merge
    {:id id
     :on-change #(on-change (.. % -target -value))
     :on-focus on-focus
     :on-blur on-blur
     :type "number"
     :placeholder placeholder
     :value value}
    attr)])


(defn select [{:keys [attr choices id on-focus on-select selected-id]
               :or {on-select #(js/console.log "no `on-select` fn. But selected:" %)
                    on-focus (fn [] (js/console.log "on-focus"))}
               :as props}]
  {:pre [(s/valid? :boxinator/countries choices)
         (string? id)]}
  [:select (merge
            {:id        id
             :on-focus  on-focus
             :class     "form-control"
             :on-change #(let [choice-id (-> % (.-target) (.-value) (medley/uuid))]
                           (on-select (get choices choice-id)))}
            attr)
   (for [{:country/keys [id name]
          :keys [selected?]}
         (cond-> choices
           selected-id (assoc-in [selected-id :selected?] true)
           :always (vals))]
     ^{:key (str "select-value-" id)}
     [:option {:value id :selected (when selected? true)}
      name])])
