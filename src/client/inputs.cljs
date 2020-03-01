(ns client.inputs
  (:require
   [clojure.spec.alpha :as s]))



(defn text [{:keys [id value on-change placeholder]
             :or {placeholder ""
                  on-change #(js/console.log "text: "  (.. % -target -value))}
             :as props}]
  [:input.form-control
   {:id id
    :on-change on-change
    :type "text"
    :placeholder placeholder
    :value value}])
