(ns common-test
  "This namespace contains functions and helpers that can be used in other test namespaces."
  (:require
   [clojure.spec.test.alpha :as stest]
   [clojure.test :as t]))


(defn test-check
  "Run test/check and return the `:pass?` value from the results."
  [x]
  (-> (stest/check x)
      first
      :clojure.spec.test.check/ret
      :pass?))
