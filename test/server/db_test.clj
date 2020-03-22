(ns server.db-test
  (:require [clojure.spec.alpha :as s]
            [clojure.test :as t]
            [clojure.test.check.generators :as gen]
            [server.db :as sut]))


(t/deftest test-save-and-get-box
  (t/testing "[INTEGRATION] Generate a box from spec, save it and the retrieve it."
    (let [{:box/keys [id] :as box} (gen/generate (s/gen :boxinator/box))]
      (sut/save-box box)
      (t/is (= box
               (get (sut/get-boxes) id))))))
