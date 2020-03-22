(ns system.shared-test
  (:require [clojure.test :as t]
            [common-test]
            [system.shared :as sut]))


(t/deftest test-normalize
  (t/testing "[UNIT] Testing normalization of a vector."
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
      [:a/id nil]         {})))

(t/deftest test-denormalize
  (t/testing "[PROPERTY] Testing on `denormalize`. It will iterate over it 1000 times."
    (t/is (common-test/test-check `sut/denormalize))))

(t/deftest test-id
  (t/testing "[UNIT] Is the `id` returning a UUID."
    (t/is (uuid? (sut/id)))))

(t/deftest test-id?
  (t/testing "[UNIT] Is the UUID an id."
    (t/is (sut/id? #uuid "b856fc6d-4fc9-419a-9f0f-ea0cdb48fea0"))))

(t/deftest test-str->id
  (t/testing "[UNIT] Test that str->id returns a id"
    (t/is (sut/id? (sut/str->id "11c8e3d3-23a5-4ebf-b55a-08054a2f0faf")))))

(t/deftest test-round-floor-to-2-deciamls
  (t/testing "[UNIT] Round an floor value."
    (t/are [args-vector expected]
        (= (apply sut/round-floor-to-2-deciamls args-vector) expected)
      [51.599999999999994] "51.60"
      [51] "51.00")))
