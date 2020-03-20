(ns integration.form-test
  (:require
   [clojure.test :as t]
   [etaoin.api :as e]
   [etaoin.keys :as k]
   [server.core]
   [server.db :as db]))

(def form-link "http://localhost:8080/#/addbox")

(defonce state
  (atom {:driver nil
         :headless? true}))

(defn driver []
  (-> @state :driver))

(defn start-driver []
  (swap! state assoc :driver (e/chrome {:headless (:headless? @state)})))

(defn stop-driver []
  (e/quit (driver))
  (swap! state assoc :driver nil))

(defn start-server []
  (server.core/start-server 8080))

(defn stop-server []
  (server.core/stop-server))

(defn go [url]
  (-> (driver)
      (e/go form-link)))

(defn fill-human [xpath text]
  (-> (driver)
      (e/fill-human xpath text)))

(defn click [xpath]
  (e/click (driver) xpath))

(defn wait [time]
  (e/wait (driver) time))

(defn fill-form []
  (go form-link)
  (fill-human "//*[@id=\"view-a-name\"]" "hejsan")
  (fill-human "//*[@id=\"view-a-weight\"]" "10")
  (click "//*[@id=\"view-a-box-colour\"]")
  (wait 1)
  (click "//*[@id=\"app\"]/div/div[3]/div[2]/div/div[1]/div[1605]")
  (click "//*[@id=\"app\"]/div/div[3]/div[2]/button")
  (click "//*[@id=\"view-a-countries\"]/option[2]")
  (click "//*[@id=\"app\"]/div/div[5]/button")
  (click "//*[@id=\"app\"]/div/div[5]/button"))

(t/deftest test-integration-form
  (t/testing "Integration test that fills the form via a webdriver. After the form is
  filled try to find the box in the database."
    (start-server)
    (start-driver)
    (fill-form)
    (stop-server)
    (stop-driver)
    (t/is (->> (db/get-boxes)
               (vals)
               (filter (fn [{:box/keys [name]}]
                         (= name "hejsan")))
               (first)
               (some?)))))


(comment
  (start-driver)
  (fill-form)
  (stop-driver)
  )
