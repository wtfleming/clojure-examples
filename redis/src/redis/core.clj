(ns redis.core
  (:require [taoensso.carmine :as car :refer (wcar)]))

(def server-connection {:pool {:max-total 10}
                        :spec {:host "localhost"
                               :port 6379
                               ;; :password "password"
                               :timeout 4000}})


(defn ping []
  (wcar server-connection (car/ping))) ;; => PONG

;; You can also use a macro to make calls a bit simpler
(defmacro wcar* [& body] `(car/wcar server-connection ~@body))

(defn ping-with-macro []
  (wcar* (car/ping))) ;; => PONG


;; Returns ["PONG" "OK" "bar"]
(defn example-commands []
  (wcar*
   (car/ping)
   (car/set "foo" "bar")
   (car/get "foo")))
