(ns server.responses
  (:require
   [ring.util.response :as response]))


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
