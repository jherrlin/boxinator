(ns client.inputs
  (:require
   [clojure.spec.alpha :as s]))



(defn text [{:keys [id value on-change placeholder]
             :or {placeholder ""
                  on-change #(js/console.log "text: "  (.. % -target -value))}
             :as props}]
  {:pre [(string? id)]}
  [:input.form-control
   {:id id
    :on-change on-change
    :type "text"
    :placeholder placeholder
    :value value}])


(defn number [{:keys [id value on-change placeholder]
               :or {placeholder ""
                    on-change #(js/console.log "numer: "  (.. % -target -value))}
               :as props}]
  {:pre [(string? id)]}
  [:input.form-control
   {:id id
    :on-change on-change
    :type "number"
    :min "0"
    :placeholder placeholder
    :value value}])
