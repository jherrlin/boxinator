(ns server.core
  (:require
   [compojure.core :refer [GET POST defroutes]]
   [compojure.route :refer [resources]]
   [config.core]
   [server.db :as db]
   [server.pages :as pages]
   [org.httpkit.server :as httpkit.server]
   [ring.middleware.defaults]
   [cheshire.core :as cheshire]
   [ring.middleware.json :as json]
   [ring.middleware.keyword-params :as middleware.keyword-params]
   [ring.middleware.edn :as edn]
   [muuntaja.middleware :as middleware]
   [ring.middleware.params :as middleware.params]
   [ring.util.response :as response]
   [taoensso.timbre :as timbre]))


(defn send-edn [response* data]
  (-> (pr-str data)
      (response*)
      (response/content-type "application/edn")))


(defroutes routes
  (GET "/" req (pages/index-html req))
  (POST "/boxes" req (fn [{:keys [body-params] :as req}]
                       (try
                         (do
                           (db/save-box body-params)
                           (send-edn response/response :ok))
                         (catch Exception e
                           (send-edn response/bad-request :fail)))))
  (GET "/boxes" req (fn [req]
                      (send-edn response/response (db/get-boxes))))
  (resources "/"))


(defn debug-middeware [handler]
  (fn [request]
    (clojure.pprint/pprint request)
    (handler request)))


(def handler
  (-> #'routes
      (debug-middeware)
      (middleware/wrap-format)))


(defn -main [& [port]]
  (let [parse-port (fn [port]
                     (try
                       (Integer/parseInt port)
                       (catch Exception e port)))
        port (or (parse-port (:port config.core/env))
                 8080)]
    (timbre/info "Server starting on port: " port)
    (httpkit.server/run-server #'handler {:port port})))


(comment
  (-main)
  )
