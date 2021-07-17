(ns json.core
  (:require [cheshire.core :as json])
  (:import [java.util Date]))


(defn -main []
  ;; generate some json
  (println (json/generate-string {:foo "bar" :baz 5}))

  (println (json/generate-string {:foo "bar" :baz nil}))

  (println (json/generate-string {:foo "bar" :baz []}))
  (println (json/generate-string {:foo "bar" :baz [1 2 "something"]}))

  ;; write some json to a stream
  (json/generate-stream {:foo "bar" :baz 5} (clojure.java.io/writer "/tmp/foo"))


  ;; generate some JSON with Dates
  ;; the Date will be encoded as a string using
  ;; the default date format: yyyy-MM-dd'T'HH:mm:ss'Z'
  (println (json/generate-string {:foo "bar" :baz (Date. 0)}))

  ;; generate some JSON with Dates with custom Date encoding
  (println (json/generate-string {:baz (Date. 0)} "yyyy-MM-dd"))


  ;; parse some json
  (json/parse-string "{\"foo\":\"bar\"}")
  ;; => {"foo" "bar"}

  ;; parse some json and get keywords back
  (json/parse-string "{\"foo\":\"bar\"}" true)
  ;; => {:foo "bar"}

  ;; parse a stream (keywords option also supported)
  (def a (json/parse-stream (clojure.java.io/reader "/tmp/foo") true))
  (println (type (:foo a)))
  ;; => java.lang.String

  (def a (json/parse-stream (clojure.java.io/reader "/tmp/foo")))
  (println (type (a "foo")))
  ;; => java.lang.String

  (def a (json/parse-stream (clojure.java.io/reader "/tmp/foo")))
  (println (type (:foo a)))
  ;; => nil
  )
