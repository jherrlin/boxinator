(ns system.boxinator
  (:require
   [clojure.spec.alpha :as s]))

(s/def ::name string?)
(s/def ::weight pos-int?)
(s/def ::r pos-int?)
(s/def ::g pos-int?)
(s/def ::color (s/keys :req [::r ::g]))
(s/def ::country #{:sweden :china :brazil :australia})
(s/def ::form
  (s/keys :req [::name
                ::weight
                ::color
                ::country]))
