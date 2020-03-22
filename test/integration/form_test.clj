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
  (swap! state assoc :driver (e/chrome {:headless (:headless? @state)
                                        :size [1000 1200]})))

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

(defn wait-visible [id]
  (e/wait-visible (driver) id))

(defn label-xpath-str [label]
  (str ".//label[contains(text(), '" label "')]"))

(defn button-xpath-str [s]
  (str "//button[contains(text(), '" s "')]"))

(defn input-xpath-str [id]
  (str ".//input[@id='" id "']"))

(defn input-element-by-label [label]
  (->> (e/get-element-attr (driver) (label-xpath-str label) :for)
       (input-xpath-str)
       (e/query (driver))))

(defn get-element-value [label]
  (->> (e/get-element-attr (driver) (label-xpath-str label) :for)
       (input-xpath-str)
       (e/get-element-value (driver))))

(defn fill-input-by-label
  [label s]
  (let [el (input-element-by-label label)]
    (while (not (= s (get-element-value label)))
      (e/fill-el (driver) el k/home (k/with-shift k/end) k/backspace)
      (e/fill-human-el (driver) el s))))

(defn click-button-with-text [s]
  (let [el (e/query (driver) (button-xpath-str s))]
    (e/click-el (driver) el)))

(defn fill-form []
  (go form-link)
  (wait-visible (label-xpath-str "Name"))
  (fill-input-by-label "Name" "hejsan")
  (fill-input-by-label "Weight" "10")
  (click "//*[@id=\"view-a-box-colour\"]")
  (wait 1)
  (click "//*[@id=\"color-picker-pallet\"]/div[100]")
  (click "//*[@id=\"color-picker-done-button\"]")
  (click "//*[@id=\"view-a-countries\"]/option[2]")
  (click-button-with-text "Validate")
  (wait-visible (button-xpath-str "Save"))
  (click-button-with-text "Save"))

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
  (start-server)
  (start-driver)
  (fill-form)
  (stop-server)
  (stop-driver)
  (db/get-boxes)
  )
