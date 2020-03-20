(ns client.forms
  "This namespace contains HOCs/components that are composed into form inputs. A form input
  as a component that is more sophisticated than a plain input. An example is `text`, it
  contains a label and a validation text if the input is not valid."
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
