(ns web-compojure.core
  (:use [compojure.core]
        [hiccup.core])
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [cheshire.core :as json]))

(defn json-response [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/json"}
   :body (json/generate-string data)})


(defroutes main-routes
  (GET "/" [] "<h1>Hello World Wide Web!</h1>")
  (GET "/hola" [] (html [:h1 "Hola World"]))
  (GET "/hola/:id" [id]
    (str "<h1>Hi " id "</h1>"))
  (GET "/hola.json" [] (json-response {:message "hello world wide web!"}))

  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (handler/site main-routes))
