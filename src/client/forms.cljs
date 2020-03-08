(ns client.forms
  (:require
   [client.inputs :as inputs]
   [client.color-picker :as color-picker]
   [client.hocs :as hocs]))


(def text
  ((comp hocs/validation-markup hocs/label hocs/focus-when-empty) inputs/text))

(def number
  ((comp hocs/validation-markup hocs/label hocs/focus-when-empty) inputs/number))

(def select
  ((comp hocs/validation-markup hocs/label) inputs/select))

(defn color-picker [{:keys [show-picker?] :as props}]
  [:div
   [text props]
   (when show-picker?
     [color-picker/color-picker props])])
