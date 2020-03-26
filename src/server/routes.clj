(ns server.routes
  (:require
   [compojure.core :as compojure]
   [compojure.route]
   [muuntaja.middleware :as middleware]
   [server.box.routes :as box.routes]
   [server.pages :as pages]))

;; (remove-ns 'server.routes)

(compojure/defroutes default
  (compojure/GET "/" req (pages/index-html req))
  (compojure.route/resources "/"))

(def routes
  (compojure/routes
   default
   box.routes/routes))

(def handler
  (-> #'routes
      (middleware/wrap-format)))
