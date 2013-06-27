(ns cascalog_examples.us_state_queries
  (:use cascalog.api)
  (:require [clojure.data.csv :as csv]
            [clj-json [core :as json]]
            [cascalog.ops :as ops]))


(def state-abbreviation-path "data/state-abbreviation.csv")
(def state-abbreviation-json-path "data/state-abbreviation.json")
(def capital-abbreviation-path "data/capital-abbreviation.csv")


(defn csv-parser [line]
  (map #(.trim %) (first (csv/read-csv line))))

(defn state-abbreviation-query
  []
  (let [file-tap (lfs-textline state-abbreviation-path)]
    (?<- 
      (stdout)
      [?abbr ?state]
      (file-tap ?line)
      (csv-parser ?line :> ?state ?abbr))))

(defn json-parser [line]
  (map (json/parse-string line) ["full" "abbr"]))

(defn state-abbreviation-json-query
  []
  (let [file-tap (lfs-textline state-abbreviation-json-path)]
    (?<-
      (stdout)
      [?abbr ?state]
      (file-tap ?line)
      (json-parser ?line :> ?state ?abbr))))


(deffilterop starts-with? [^String s prefix] (.startsWith s prefix))

(defn state-starting-with-query
  "Output states starting with the prefix. Ie V -> Vermont, Virginia"
  [prefix]
  (let [file-tap (lfs-textline state-abbreviation-path)]
    (?<-
      (stdout)
      [?state]
      (file-tap ?line)
      (csv-parser ?line :> ?state _)
      (starts-with? ?state prefix))))
      

(defn count-state-starting-with-query
  "Output count of states starting with the prefix. Ie V -> 2"
  [prefix]
  (let [file-tap (lfs-textline state-abbreviation-path)]
    (?<-
      (stdout)
      [?count]
      (file-tap ?line)
      (csv-parser ?line :> ?state _)
      (starts-with? ?state prefix)
      (ops/count ?count))))




(defn join-state-capital-query
  []
  (let [cap-abbr-tap  (lfs-textline capital-abbreviation-path)
        st-abbr-tap   (lfs-textline state-abbreviation-path)]
    (?<-
      (stdout)
      [?state ?capital]
      (cap-abbr-tap ?cap-abbr-line)
      (csv-parser ?cap-abbr-line :> ?capital ?abbr)
      (st-abbr-tap ?st-abbr-line)
      (csv-parser ?st-abbr-line :> ?state ?abbr))))



(defn first-letter [^String word] (first word))

(defn state-first-letter-count
  []
  (let [file-tap (lfs-textline state-abbreviation-path)]
    (?<-
      (stdout)
      [?first-letter ?letter-count]
      (file-tap ?line)
      (csv-parser ?line :> ?state _)
      (first-letter ?state :> ?first-letter)
      (ops/count ?letter-count))))
      

