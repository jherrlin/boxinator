(ns client.db)


(def countries
  {#uuid "958e0376-eb26-428a-8147-7efc04e8d3e5"
   #:country{:id #uuid "958e0376-eb26-428a-8147-7efc04e8d3e5",
             :multiplier 1.3,
             :name "Sweden"},
   #uuid "837225a9-f74d-447e-87bc-49c0b58ec972"
   #:country{:id #uuid "837225a9-f74d-447e-87bc-49c0b58ec972",
             :multiplier 4,
             :name "China"},
   #uuid "b1ace9ef-c1fa-4c00-94fc-97db4618c245"
   #:country{:id #uuid "b1ace9ef-c1fa-4c00-94fc-97db4618c245",
             :multiplier 8.6
             :name "Brazil"},
   #uuid "8b759afd-1e0e-40ef-aecb-a3e48db4056e"
   #:country{:id #uuid "8b759afd-1e0e-40ef-aecb-a3e48db4056e",
             :multiplier 7.2
             :name "Australia"}})


(def app-db
  {:color nil
   :countries countries})
