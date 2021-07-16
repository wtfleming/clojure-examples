(ns core-async-examples.core
    (:require
     [clojure.core.async :refer [chan sliding-buffer go go-loop timeout >! <!]]))

(defn create-consumer
  "Creates a channel that prints to stdout any data sent to it"
  [name]
  (let [in (chan (sliding-buffer 64))]
    (go-loop [data (<! in)]
      (when data
        (println (format "consumer %s received: %s" name data))
        (recur (<! in))))
    in))

(defn generate-messages
  "Returns a lazy seq of n random integers"
  [n]
  (take n (repeatedly #(rand-int 100))))

(defn create-producer
  "Creates a producer that sends 5 random numbers to all provided channels"
  [& channels]
  (go
    (doseq [msg (generate-messages 5)
            out channels]
      (<! (timeout 300)) ;; pause for 300 msecs
      (>! out msg))))

;; Run this in a repl
;; (create-producer (create-consumer "a") (create-consumer "b"))
;; And you will see output like this:
;; consumer a received: 49
;; consumer b received: 49
;; consumer a received: 12
;; consumer b received: 12
;; consumer a received: 41
;; consumer b received: 41
;; consumer a received: 76
;; consumer b received: 76
;; consumer a received: 97
;; consumer b received: 97

