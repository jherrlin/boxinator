(ns server.core
  (:require
   [compojure.core :refer [GET POST routes defroutes]]
   [compojure.route :refer [resources]]
   [config.core]
   [server.routes :as routes]
   [server.db :as db]
   [server.pages :as pages]
   [org.httpkit.server :as httpkit.server]
   [ring.middleware.defaults]
   [muuntaja.middleware :as middleware]
   [ring.util.response :as response]))


(defonce state (atom {:stop-server-fn nil}))

(defn start-server
  "Connect a new db instance and start the server."
  [port]
  (db/connect!)
  (swap! state assoc :stop-server-fn
         (httpkit.server/run-server #'routes/handler {:port port})))

(defn stop-server
  "Stop server."
  []
  (if-let [stop-server-fn (some-> @state (:stop-server-fn))]
    (do
      (db/disconnect!)
      (stop-server-fn))
    (println "Server instance not found! Is it started?")))

(defn -main
  "Main entry to start the server."
  [& [port]]
  (let [parse-port (fn [port]
                     (try
                       (Integer/parseInt port)
                       (catch Exception e port)))
        port (or (parse-port (:port config.core/env))
                 8080)]
    (println "Server starting on port: " port)
    (start-server port)))

(comment
  (-main)
  (stop-server)
  )
