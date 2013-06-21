(ns cascalog_examples.read_file
  (:use cascalog.api)
  (:require [clojure.data.csv :as csv]))

(def state-abbreviation-path "data/state-abbreviation.csv")


(defn print-file
  "Use cascalog to print a file"
  [path-to-file]
  (let [file-tap (lfs-textline path-to-file)]
    (?<-
      (stdout)
      [?line]
      (file-tap :> ?line))))



(defn csv-parser [line]
  (map #(.trim %) (first (csv/read-csv line))))

(defn print-csv-file
  "Use cascalog to print a csv file"
  [path-to-file]
  (let [file-tap (lfs-textline path-to-file)]
    (?<-
      (stdout)
      [?abbreviation ?state]
      (file-tap :> ?line)
      (csv-parser ?line :> ?state ?abbreviation))))
