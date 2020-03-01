(ns client.forms
  (:require
   [client.inputs :as inputs]
   [client.hocs :as hocs]))


(def text
  ((comp hocs/validation-markup hocs/label hocs/focus-when-empty) inputs/text))

(def number
  ((comp hocs/validation-markup hocs/label hocs/focus-when-empty) inputs/number))

(def select
  ((comp hocs/validation-markup hocs/label) inputs/select))
