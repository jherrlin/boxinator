(ns server.pages
  (:require
   [hiccup.page :refer [html5 include-js include-css]]
   [clojure.java.io :as io]
   [clojure.edn :as edn]))


(defn read-edn-file [resource-filesystem-path]
  (try
    (edn/read-string (slurp (io/resource resource-filesystem-path)))
    (catch java.io.IOException e
      (printf "Couldn't open '%s': %s\n" resource-filesystem-path (.getMessage e)))
    (catch Exception e
      (printf "Error parsing edn file '%s': %s\n" resource-filesystem-path (.getMessage e)))))


(defn index-html [req]
  (html5 {:style "height: 100%"}
   [:head
    [:meta {:charset "UTF-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1, shrink-to-fit=no"}]
    (include-css "//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.5/css/bootstrap.css")
    (include-css "vendor/css/material-design-iconic-font.min.css")
    (include-css "vendor/css/re-com.css")
    (include-css "//fonts.googleapis.com/css?family=Roboto:300,400,500,700,400italic")
    (include-css "//fonts.googleapis.com/css?family=Roboto+Condensed:400,300")]
   [:body {:style "height: 100%"}
    [:div#app "asd"]
    (->> "public/js/manifest.edn"
         (read-edn-file)
         (map :output-name)
         (mapv #(str "js/" %))
         (apply include-js))]))