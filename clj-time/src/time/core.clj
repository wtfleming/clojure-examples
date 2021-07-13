(ns time.core
  (:gen-class)
  (:require
    [clj-time.core :as timec]
    [clj-time.coerce :as coerce]
    [clj-time.format :as time-format]))


(def ^:dynamic *default-formats*
  [ :date
    :date-hour-minute
    :date-hour-minute-second
    :date-hour-minute-second-ms
    :date-time
    :date-time-no-ms
    :rfc822
    "YYYY-MM-dd HH:mm"
    "YYYY-MM-dd HH:mm:ss"
    "dd/MM/YYYY"
    "YYYY/MM/dd"
    "d MMM YYYY"])

(defprotocol ToFormatter
  (->formatter [fmt]))

(extend-protocol ToFormatter
  java.lang.String
  (->formatter [fmt]
    (time-format/formatter fmt))

  clojure.lang.Keyword
  (->formatter [fmt]
    (time-format/formatters fmt)))


(defn parse-or-nil
  [fmt date-str]
  (try
    (time-format/parse
      (->formatter fmt)
      date-str)
    (catch
      Exception e
      nil)))

;; Attempt to parse all known formats, take the first valid one
(defn normalize-datetime
  [date-str]
  (first
    (remove nil?
      (map #(parse-or-nil % date-str) *default-formats*))))


(defn normalize-datetime-examples []
  (println "\n------ normalize-datetime-examples ------")

  ;; #<DateTime 2014-01-22T00:00:00.000Z>
  (println (normalize-datetime "2014-01-22"))

  ;; #<DateTime 2014-01-22T00:00:00.000Z>
  (println (normalize-datetime "2014/01/22"))

  ;; #<DateTime 2014-01-22T00:00:00.000Z>
  (println (normalize-datetime "22 Jan 2014"))

  ;; #<DateTime 2014-01-22T14:25:00.000Z>
  (println (normalize-datetime "2014-01-22 14:25")))

(defn parse-time-examples []
  (println "\n------ parse-time-examples ------")
  (let [time-format-example (time-format/formatter "MM/dd/yy")]
    (println "MM/dd/yy:" (time-format/parse time-format-example "03/15/21"))))

(defn relative-dates-examples []
  (println "\n------ relative-dates-examples ------")

  (print "1 day from now: ")
  (-> 1
      timec/days
      timec/from-now
      println)

  (print "2 days ago: ")
  (-> 2
      timec/days
      timec/ago
      println))

(defn -main []
  (println "Current time:" (timec/now))

  ;; #<DateTime 1998-04-25T00:00:00.000Z>
  (println "from-long:" (coerce/from-long 893462400000))

  (parse-time-examples)

  (relative-dates-examples)

  (normalize-datetime-examples))
