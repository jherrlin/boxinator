(ns system.boxinator-test
  (:require
   [clojure.spec.test.alpha :as stest]
   [clojure.test :as t]
   [common-test]
   [system.boxinator :as sut]
   [system.shared :as shared]))


(t/deftest test-normalize-boxes
  (t/testing "[PROPERTY] Testing on `normalize-boxes`. It will iterate over it 1000 times."
    (t/is (common-test/test-check `sut/normalize-boxes))))

(t/deftest test-total-cost
  (t/testing "[UNIT] Testing that `total-cost` works."
      (t/is (= 126.0
               (sut/total-cost
                [#:box{:color #:color{:g 2, :r 2},
                       :country #uuid "b1ace9ef-c1fa-4c00-94fc-97db4618c245"
                       :id #uuid "84825c36-538d-4d19-969e-d3bbec096b47",
                       :weight 10 ;; 86
                       :name "a",}
                 #:box{:color #:color{:g 2, :r 2},
                       :country #uuid "837225a9-f74d-447e-87bc-49c0b58ec972"
                       :id #uuid "671ac9fa-2866-4455-8544-52e1c19768ff",
                       :weight 10 ;; cost 40
                       :name "X"}])))))

(t/deftest test-total-weight
  (t/testing "[UNIT] Testing that `total-weight` works."
      (t/is (= 20
               (sut/total-weight
                [#:box{:color #:color{:g 2, :r 2},
                       :country #uuid "b1ace9ef-c1fa-4c00-94fc-97db4618c245"
                       :id #uuid "84825c36-538d-4d19-969e-d3bbec096b47",
                       :weight 10 ;; 86
                       :name "a",}
                 #:box{:color #:color{:g 2, :r 2},
                       :country #uuid "837225a9-f74d-447e-87bc-49c0b58ec972"
                       :id #uuid "671ac9fa-2866-4455-8544-52e1c19768ff",
                       :weight 10 ;; cost 40
                       :name "X"}])))))

(t/deftest test-assoc-boxes-attributes
  (t/testing "[UNIT] Testing that assoc temp values are correct"
    (let [altered-boxes (sut/assoc-boxes-attributes
                         [#:box{:color #:color{:g 0, :r 0},
                                :country #uuid "b1ace9ef-c1fa-4c00-94fc-97db4618c245"
                                :id #uuid "84825c36-538d-4d19-969e-d3bbec096b47",
                                :weight 10
                                :name "a"}])
          {:keys [background-color shipping-cost]} (->> altered-boxes (first))]
      (t/is (= background-color "rgb(0,0,0)"))
      (t/is (= shipping-cost "86.00")))))

(t/deftest test-assoc-box-form
  (t/testing "[UNIT] Populare the db with a `:boxinator/box` form containing default values."
    (t/is (-> (sut/assoc-box-form {})
              :form :boxinator/box :values :box/id
              (shared/id?)))))
