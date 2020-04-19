(ns jokes.core
  (:require [clj-http.client :as client])
  (:require [cheshire.core :refer :all])
  (:require [clojure.java.jdbc :as j])
  (:gen-class))

(def service-url "https://sv443.net/jokeapi/v2/joke/Any")

(def mysql-db {:dbtype "mysql"
               :host "localhost"
               :port "3306"
               :dbname "clojure"
               :user "root"
               :password "12345678"})

(defn insert! 
  [joke]
  (if (not (= nil (:setup joke)))
    (j/insert! mysql-db :jokes 
        { 
          :category (:category joke)
          :type_name (:type joke)
          :setup (:setup joke)
          :delivery (:delivery joke) 
          :nsfw (:nsfw (:flags joke))
          :religious (:religious (:flags joke))
          :racist (:racist (:flags joke))
          :sexist (:sexist (:flags joke))
        }
    )
    (println "Cannot insert joke " (:id joke) " cuz its setup is null")
  )
)

(defn get-joke 
  []
  (client/get service-url))

(defn read-api-response
  [json]
  (parse-string (str json) true))

(defn save-joke 
  [joke]
  (insert! (read-api-response joke)))

(defn get-50-jokes
  []
  (dotimes [n 50]
    (save-joke (:body (get-joke)))))


(defn -main
  [& args]  
  (println "Loading...")
  (get-50-jokes)
  (println "Done!"))
