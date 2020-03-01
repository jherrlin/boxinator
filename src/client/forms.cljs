(ns client.forms
  (:require
   [client.inputs :as inputs]
   [client.hocs :as hocs]))


(def text-input ((comp hocs/validation-markup hocs/label hocs/focus-when-empty) inputs/text))
(def number-input ((comp hocs/validation-markup hocs/label hocs/focus-when-empty) inputs/number))
