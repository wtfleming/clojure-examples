(ns hello-world-kafka.core
  (:import (kafka.consumer Consumer ConsumerConfig KafkaStream)
           (kafka.producer KeyedMessage ProducerConfig)
           (kafka.javaapi.producer Producer)
           (java.util Properties)
           (java.util.concurrent Executors))
  (:gen-class))


;; ---------- Producer ----------
(defn- create-producer
  "Creates a producer that can be used to send a message to Kafka"
  [brokers]
  (let [props (Properties.)]
    (doto props
      (.put "metadata.broker.list" brokers)
      (.put "serializer.class" "kafka.serializer.StringEncoder")
      (.put "request.required.acks" "1"))
    (Producer. (ProducerConfig. props))))

(defn- send-to-producer
  "Send a string message to Kafka"
  [producer topic message]
  (let [data (KeyedMessage. topic nil message)]
    (.send producer data)))


;; ---------- Consumer ----------
(defrecord KafkaMessage [topic offset partition key value-bytes])

(defn- create-consumer-config
  "Returns a configuration for a Kafka client."
  []
  (let [props (Properties.)]
    (doto props
      (.put "zookeeper.connect" "127.0.0.1:2181")
      (.put "group.id" "group1")
      (.put "zookeeper.session.timeout.ms" "400")
      (.put "zookeeper.sync.time.ms" "200")
      (.put "auto.commit.interval.ms" "1000"))
    (ConsumerConfig. props)))

(defn- consume-messages
  "Continually consume messages from a Kafka topic and write message value to stdout."
  [stream thread-num]
  (let [it (.iterator ^KafkaStream stream)]
    (println (str "Starting thread " thread-num))
    (while (.hasNext it)
      (as-> (.next it) msg
            (KafkaMessage. (.topic msg) (.offset msg) (.partition msg) (.key msg) (.message msg))
            (println (str "Received on thread " thread-num ": " (String. (:value-bytes msg))))))
    (println (str "Stopping thread " thread-num))))

(defn- start-consumer-threads
  "Start a thread for each stream."
  [thread-pool kafka-streams]
  (loop [streams kafka-streams
         index 0]
    (when (seq streams)
      (.submit thread-pool (cast Callable #(consume-messages (first streams) index)))
      (recur (rest streams) (inc index)))))


;; ---------- Application ----------
;; $ lein trampoline run random_numbers 2
(defn -main
  "Pull messages from a Kafka topic using the High Level Consumer"
  [topic num-threads]
  (let [consumer (Consumer/createJavaConsumerConnector (create-consumer-config))
        consumer-map (.createMessageStreams consumer {topic (Integer/parseInt num-threads)})
        kafka-streams (.get consumer-map topic)
        thread-pool (Executors/newFixedThreadPool (Integer/parseInt num-threads))
        producer (create-producer "127.0.0.1:9092")]

    ;; Clean up on a SIGTERM or Ctrl-C
    (.addShutdownHook (Runtime/getRuntime)
                      (Thread. #(do (.shutdown consumer)
                                    (.shutdown thread-pool))))

    ;; Connect and start listening for messages on Kafka
    (start-consumer-threads thread-pool kafka-streams)

    ;; Send a random int to Kafka every 500 milliseconds
    (loop []
      (let [num (str (rand-int 1000))]
        (println (str "Sending to Kafka topic " topic ": " num))
        (send-to-producer producer topic num)
        (Thread/sleep 500)
        (recur)))))
