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

(t/deftest test-id
  (t/is (uuid? (sut/id))))

(t/deftest test-id?
  (t/is (sut/id? #uuid "b856fc6d-4fc9-419a-9f0f-ea0cdb48fea0")))
