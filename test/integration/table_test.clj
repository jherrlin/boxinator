(ns integration.table-test
  (:require
   [clojure.spec.alpha :as s]
   [clojure.test :as t]
   [clojure.test.check.generators :as gen]
   [etaoin.api :as e]
   [server.core]
   [integration.shared-test :as shared]
   [server.db :as db]))

(def table-link "http://localhost:8080/#/listboxes")
(def rendered-str "<td>nSq36TN5Zc5IUf0G5N8T221GFMUl0</td><td>5847902 kilograms</td><td style=\"background-color: rgb(180, 0, 0);\"></td><td>7602272.60 Sek</td>")

(t/deftest test-table-view
  (t/testing "[INTEGRATION] Test that 3 saved boxes exists in the table view. Ensure that
  the generated HTML for `box1` is as expected."

    (let [box1 #:box{:color #:color{:g 0, :r 180},
                       :country #uuid "958e0376-eb26-428a-8147-7efc04e8d3e5",
                       :id #uuid "0b490aef-3b5d-427e-a76c-8b42e2d33853",
                       :name "nSq36TN5Zc5IUf0G5N8T221GFMUl0",
                       :weight 5847902}
          box2 (gen/generate (s/gen :boxinator/box))
          box3 (gen/generate (s/gen :boxinator/box))]

      (shared/start-server)
      (shared/start-driver)

      (db/save-box box1)
      (db/save-box box2)
      (db/save-box box3)

      (shared/go table-link)
      (shared/wait 2)

      (t/is (some? (e/query (shared/driver) {:id (str (:box/id box1))})))
      (t/is (some? (e/query (shared/driver) {:id (str (:box/id box2))})))
      (t/is (some? (e/query (shared/driver) {:id (str (:box/id box3))})))

      (t/is
       (= rendered-str
          (e/get-element-inner-html (shared/driver) {:id (str (:box/id box1))})))

      (shared/stop-driver)
      (shared/stop-server))))
