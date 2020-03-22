(ns system.country-test
  (:require [clojure.test :as t]
            [common-test]
            [system.country :as sut]))


(t/deftest test-normalize-countries
  (t/testing "[PROPERTY] Testing on `normalize-countries`. It will iterate over it 1000 times."
    (t/is (common-test/test-check `sut/normalize-countries))))

(t/deftest test-country
  (t/testing "[UNIT] Get country from a id."
      (t/is (= (sut/country #uuid "958e0376-eb26-428a-8147-7efc04e8d3e5")
               #:country{:id #uuid "958e0376-eb26-428a-8147-7efc04e8d3e5",
                         :multiplier 1.3,
                         :name "Sweden"}))))

(t/deftest test-multiplier
  (t/testing "[UNIT] Get multiplier from country id."
    (t/is (= (sut/multiplier #uuid "b1ace9ef-c1fa-4c00-94fc-97db4618c245")
             8.6))))
