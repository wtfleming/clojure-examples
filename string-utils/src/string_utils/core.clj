(ns string-utils.core
  (:require [inflections.core :as i]))


; Pluralize ---------------------------------
; https://github.com/r0man/inflections-clj
(defn pluralize []
  (println (i/pluralize 1 "dog")) ; "1 dog"
  (println (i/pluralize 3 "dog")) ; "3 dogs"
  (println (i/pluralize 1 "box" "boxen")) ; "1 box"
  (println (i/pluralize 4 "box" "boxen"))) ; "4 boxen"


; Tableify ---------------------------------
(defn tableify
  "Create a string formatted as a 3 column table from a sequence"
  [row]
  (apply format "%-20s | %-20s | %-20s" row))

(def header ["First name" "Last name" "Year of Birth"])
(def people [["Ada" "Lovelace" 1815]
             ["Haskell" "Curry" 1900]
             ["Fred" "Brooks" 1931]])

(defn output-table! []
  (->> people
       (concat [header])
       (map tableify)
       (map println)
       (dorun)))

; First name           |            Last name |        Year of Birth
; Ada                  |             Lovelace |                 1815
; Haskell              |                Curry |                 1900
; Fred                 |               Brooks |                 1931
