(ns server.core
  (:require
   [compojure.core :refer [GET POST defroutes]]
   [compojure.route :refer [resources]]
   [config.core]
   [server.db :as db]
   [server.pages :as pages]
   [org.httpkit.server :as httpkit.server]
   [ring.middleware.defaults]
   [muuntaja.middleware :as middleware]
   [ring.util.response :as response]))


(defonce state (atom {:stop-server-fn nil}))

(defn edn-response
  "Create HTTP response with edn body.
  - `response*` is the type of HTTP response (optional).
  - `x`         is Clojure datastructure."
  ([x]
   (edn-response response/response x))
  ([response* x]
   (-> (pr-str x)
       (response*)
       (response/content-type "application/edn"))))

(defroutes routes
  (GET "/" req (pages/index-html req))
  (POST "/box" req (fn [{:keys [body-params] :as req}]
                     (db/save-box body-params)
                     (edn-response (db/get-boxes))))
  (GET "/boxes" req (fn [req]
                      (edn-response (db/get-boxes))))
  (resources "/"))

(def handler
  (-> #'routes
      (middleware/wrap-format)))

(defn start-server
  "Connect a new db instance and start the server."
  [port]
  (db/connect!)
  (swap! state assoc :stop-server-fn
         (httpkit.server/run-server #'handler {:port port})))

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
