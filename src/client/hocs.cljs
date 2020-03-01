(ns client.hocs
  (:require
   [reagent.core :as reagent]))


(defn label [hoc-component]
  (fn [{:keys [label error-text required? valid? show-validation?]
        :or {show-validation? false
             required? false}
        :as props}]
    {:pre [(string? label)]}
    [:div
     [:label {:style {:color (when (and (not valid?)
                                        error-text
                                        show-validation?)
                               "#a94442")}}
      (str (when required? "* ") label)]
     [hoc-component props]]))


(defn focus-when-empty [hoc-component]
  (fn [{:keys [value id focus?] :as props}]
    {:pre [(string? id)]}
    (reagent/create-class
     {:display-name "focus-when-empty"
      :component-did-mount
      (fn []
        (when (and (empty? value)
                   focus?)
          (js/requestAnimationFrame
           #(.focus (.getElementById js/document id)))))

      :reagent-render
      (fn []
        [hoc-component props])})))


(defn validation-markup [hoc-component]
  (fn [{:keys [valid? error-text show-validation? visited?] :as props}]
    [:div {:class (str "form-group has-feedback"
                       (when (and show-validation?
                                  visited?)
                         (if valid?
                           " has-success"
                           " has-error")))}
     [hoc-component props]
     (when (and show-validation?
                visited?
                (not valid?))
       [:p {:style {:color (when-not valid? "#a94442")}}
        error-text])]))
