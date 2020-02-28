(ns server.core
  (:require
   [compojure.core :refer [GET POST defroutes]]
   [compojure.route :refer [resources]]
   [config.core]
   [server.pages :as pages]
   [org.httpkit.server :as httpkit.server]
   [ring.middleware.defaults]
   [ring.middleware.keyword-params :as middleware.keyword-params]
   [ring.middleware.params :as middleware.params]
   [taoensso.timbre :as timbre]))



(defroutes routes
  (GET "/" req (pages/index-html req))
  (resources "/"))


(def handler
  (-> #'routes
      (ring.middleware.defaults/wrap-defaults
       ring.middleware.defaults/site-defaults)))


(defn -main [& [port]]
  (let [parse-port (fn [port]
                     (try
                       (Integer/parseInt port)
                       (catch Exception e port)))
        port (or (parse-port (:port config.core/env))
                 3000)]
    (timbre/info "Server starting on port: " port)
    (println "Server starting on port: " port)
    (httpkit.server/run-server handler {:port port})))


(comment
  (-main)
  )
