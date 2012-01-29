(ns json.core
  (:use [cheshire.core])
  (:import [java.util Date]))
  
;; https://github.com/dakrone/cheshire  
  
(defn -main []
  ;; generate some json
  (println (generate-string {:foo "bar" :baz 5}))
  
  (println (generate-string {:foo "bar" :baz nil}))
  
  (println (generate-string {:foo "bar" :baz []}))
  (println (generate-string {:foo "bar" :baz [1 2 "something"]}))
  
  ;; write some json to a stream
  (generate-stream {:foo "bar" :baz 5} (clojure.java.io/writer "/tmp/foo"))
  
  
  ;; generate some JSON with Dates
  ;; the Date will be encoded as a string using
  ;; the default date format: yyyy-MM-dd'T'HH:mm:ss'Z'
  (println (generate-string {:foo "bar" :baz (Date. 0)}))

  ;; generate some JSON with Dates with custom Date encoding
  (println (generate-string {:baz (Date. 0)} "yyyy-MM-dd"))
  
  
  
  ;; parse some json
  (parse-string "{\"foo\":\"bar\"}")
  ;; => {"foo" "bar"}
  
  ;; parse some json and get keywords back
  (parse-string "{\"foo\":\"bar\"}" true)
  ;; => {:foo "bar"}
  
  ;; parse a stream (keywords option also supported)  
  (def a (parse-stream (clojure.java.io/reader "/tmp/foo") true))
  (println (type (:foo a)))
  ;; => java.lang.String
  
  (def a (parse-stream (clojure.java.io/reader "/tmp/foo")))
  (println (type (a "foo")))
  ;; => java.lang.String
  
  (def a (parse-stream (clojure.java.io/reader "/tmp/foo")))
  (println (type (:foo a)))
  ;; => nil
  
  
  )
