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
   [ring.util.response :as response]
   [taoensso.timbre :as timbre]))


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
  (POST "/boxes" req (fn [{:keys [body-params] :as req}]
                       (db/save-box body-params)
                       (edn-response (db/get-boxes))))
  (GET "/boxes" req (fn [req]
                      (edn-response (db/get-boxes))))
  (resources "/"))

(defn debug-middeware
  "Debug middleware"
  [handler]
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
