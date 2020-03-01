(ns client.db)


(def countries
  {#uuid "5ab0c590-e6f6-41ae-bbcc-f3155e04e74f" {:id #uuid "5ab0c590-e6f6-41ae-bbcc-f3155e04e74f" :name "Sweden"}
   #uuid "747b3c56-d7c7-49e0-8b4f-7bac81d013d3" {:id #uuid "747b3c56-d7c7-49e0-8b4f-7bac81d013d3" :name "China"}
   #uuid "981a8b1e-df7d-4c0a-89ea-e2f36f7a9d2b" {:id #uuid "981a8b1e-df7d-4c0a-89ea-e2f36f7a9d2b" :name "Brazil"}
   #uuid "79c5220b-7ffe-420f-bb0f-8e31ac0844a3" {:id #uuid "79c5220b-7ffe-420f-bb0f-8e31ac0844a3" :name "Austalia"}})


(def app-db
  {:color {:r 255
           :g 255}
   :countries countries})
