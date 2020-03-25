(ns server.box.routes
  (:require
   [compojure.core :as compojure :refer [GET DELETE POST PUT]]
   [compojure.route]
   [server.box.controls :as controls]))


(compojure/defroutes routes
  (compojure/context "/api/" []
    (GET    "/boxes"   {}                    (controls/read))
    (GET    "/box/:id" [id]                  (controls/read id))
    (POST   "/box"     {:keys [body-params]} (controls/create body-params))
    (PUT    "/box"     {:keys [body-params]} (controls/create body-params))
    (DELETE "/box/:id" [id]                  (controls/delete id))))
