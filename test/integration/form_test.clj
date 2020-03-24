(ns integration.form-test
  (:require
   [clojure.test :as t]
   [integration.shared-test :as shared]
   [server.core]
   [server.db :as db]))

(def form-link "http://localhost:8080/#/addbox")

(defn fill-form []
  (shared/go form-link)
  (shared/wait-visible (shared/label-xpath-str "Name"))
  (shared/fill-input-by-label "Name" "hejsan")
  (shared/fill-input-by-label "Weight" "10")
  (shared/click "//*[@id=\"view-a-box-colour\"]")
  (shared/wait 1)
  (shared/click "//*[@id=\"color-picker-pallet\"]/div[100]")
  (shared/click "//*[@id=\"color-picker-done-button\"]")
  (shared/click "//*[@id=\"view-a-countries\"]/option[2]")
  (shared/click-button-with-text "Validate")
  (shared/wait-visible (shared/button-xpath-str "Save"))
  (shared/click-button-with-text "Save"))

(t/deftest test-integration-form
  (t/testing "[INTEGRATION] Test that fills the form via a webdriver. After the form is
  filled try to find the box in the database."
    (shared/start-server)
    (shared/start-driver)
    (fill-form)
    (shared/wait 1)
    (t/is (->> (db/get-boxes)
               (vals)
               (filter (fn [{:box/keys [name]}]
                         (= name "hejsan")))
               (first)
               (some?)))
    (shared/stop-driver)
    (shared/stop-server)))
