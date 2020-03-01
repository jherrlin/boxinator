(ns client.forms
  (:require
   [client.inputs :as inputs]
   [client.hocs :as hocs]))


(def input ((comp hocs/validation-markup hocs/label hocs/focus-when-empty) inputs/text))
