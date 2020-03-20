(ns system.boxinator-test
  (:require [clojure.spec.test.alpha :as stest]
            [clojure.test :as t]
            [common-test]
            [system.boxinator :as sut]))


(t/deftest test-normalize-boxes
  (t/is (common-test/test-check `sut/normalize-boxes)))

(t/deftest test-total-cost
  (t/is (= 126.0
           (sut/total-cost
            [#:box{:color #:color{:g 2, :r 2},
                   :country #uuid "b1ace9ef-c1fa-4c00-94fc-97db4618c245"
                   :id #uuid "84825c36-538d-4d19-969e-d3bbec096b47",
                   :weight 10 ;; 86
                   :name "",}
             #:box{:color #:color{:g 2, :r 2},
                   :country #uuid "837225a9-f74d-447e-87bc-49c0b58ec972"
                   :id #uuid "671ac9fa-2866-4455-8544-52e1c19768ff",
                   :weight 10 ;; cost 40
                   :name "X"}]))))

(t/deftest test-total-weight
  (t/is (= 20
           (sut/total-weight
            [#:box{:color #:color{:g 2, :r 2},
                   :country #uuid "b1ace9ef-c1fa-4c00-94fc-97db4618c245"
                   :id #uuid "84825c36-538d-4d19-969e-d3bbec096b47",
                   :weight 10 ;; 86
                   :name "",}
             #:box{:color #:color{:g 2, :r 2},
                   :country #uuid "837225a9-f74d-447e-87bc-49c0b58ec972"
                   :id #uuid "671ac9fa-2866-4455-8544-52e1c19768ff",
                   :weight 10 ;; cost 40
                   :name "X"}]))))
