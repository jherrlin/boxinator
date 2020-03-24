(ns integration.shared-test
  (:require
   [clojure.spec.alpha :as s]
   [clojure.test :as t]
   [clojure.test.check.generators :as gen]
   [etaoin.api :as e]
   [etaoin.keys :as k]
   [server.core]
   [server.db :as db]))


(defonce state
  (atom {:driver nil
         :headless? true}))

(defn driver []
  (-> @state :driver))

(defn start-driver []
  (swap! state assoc :driver (e/chrome {:headless (:headless? @state)
                                        :size [1000 1200]
                                        :args ["--no-sandbox" "--disable-dev-shm-usage"]})))

(defn stop-driver []
  (e/quit (driver))
  (swap! state assoc :driver nil))

(defn start-server []
  (server.core/start-server 8080))

(defn stop-server []
  (server.core/stop-server))

(defn go [url]
  (-> (driver)
      (e/go url)))

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


(comment
  (start-server)
  (start-driver)
  (fill-form)
  (stop-server)
  (stop-driver)
  (db/get-boxes)
  )
