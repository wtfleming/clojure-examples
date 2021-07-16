(ns core-async-examples.core
    (:require
     [clojure.core.async :refer [chan buffer offer! go go-loop timeout >! <!]]))

;; Demonstrates dropping requests when a buffer is full.
;; If you are ok with elements being dropped, you could use a dropping-buffer
;; or sliding-buffer instead.
;; But doing it this way would allow us to communicate back to a user
;; that their request failed

(defn- create-consumer
  "Creates a channel that prints to stdout any data sent to it"
  []
  (let [in (chan (buffer 2))]
    (go-loop [data (<! in)]
      (when data
        (println (format "consumer received: %s" data))
        (<! (timeout 50)) ;; simulate 50 ms of work being done
        (recur (<! in))))
    in))

(defn- send-message [channel message]
  (if-let [result (offer! channel message)]
    (println (format "send success: %s" message))
    (println (format "send DROPPED: %s" message))))


(defn send-messages []
  (println "!!!")
  (let [consumer (create-consumer)]
      (send-message consumer "a")
      (send-message consumer "b")
      (send-message consumer "c")   ;; buffer should be full, and this request will be dropped
      (Thread/sleep 1000)
      (send-message consumer "d"))) ;; buffer should have room by now and this message will be processed


;; Run the following in a repl
;; (send-messages)
;; And the output should be:
;; send success: a
;; send success: b
;; send DROPPED: c
;; consumer received: a
;; consumer received: b
;; send success: d
;; consumer received: d
