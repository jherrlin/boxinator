(ns system.shared-test
  (:require [clojure.test :as t]
            [common-test]
            [system.shared :as sut]))


(t/deftest test-normalize
  (t/are [args-vector expected]
      (= (apply sut/normalize args-vector) expected)
    [:a/id [{:a/id 1}
            {:a/id 2}]]
    {1 {:a/id 1}
     2 {:a/id 2}}
    [:a/id [{:a/id 1}]] {1 {:a/id 1}}
    [:a/id {}]          {}
    [nil nil]           nil
    [nil {}]            nil
    [:a/id nil]         {}))

(t/deftest test-denormalize
  (t/is (common-test/test-check `sut/denormalize)))
