(ns web-compojure.core
  (:use [compojure.core]
        [hiccup.core]
        [cheshire.core])
  (:require [compojure.route :as route]
            [compojure.handler :as handler]))

(defroutes main-routes
  (GET "/" [] "<h1>Hello World Wide Web!</h1>")
  (GET "/hola" [] (html [:h1 "Hola World"]))

  (GET "/hola.json" [] {:status 200
                        :headers {"Content-Type" "application/json"}
                        :body (generate-string {:message "hello world wide web"})})


  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (handler/site main-routes))
