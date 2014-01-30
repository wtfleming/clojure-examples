(ns diff.core
  (:require [clj-diff.core :as diff]))

;; https://github.com/brentonashworth/clj-diff

(def fuzzy-max-diff 2)
(def fuzzy-percent-diff 0.1)
(def fuzzy-dist diff/edit-distance)
;(def fuzzy-dist diff/levenshtein-distance)

(defn fuzzy=
  "Returns a fuzzy match."
  [a b]
  (let [dist (fuzzy-dist a b)]
    (or
      (<= dist fuzzy-max-diff)
      (<= (/ dist (min (count a) (count b)))
        fuzzy-percent-diff))))


(defn records-match?
  [key-fn a b]
  (let [kfns (if (sequential? key-fn)
               key-fn [key-fn])
         rfn (fn [prev next-fn]
               (and prev (fuzzy= (next-fn a)
                           (next-fn b))))]
    (reduce rfn true kfns)))

(def data
  {
    :mulder  {:given-name "Fox"  :surname "Mulder"}
    :molder  {:given-name "Fox"  :surname "Molder"}
    :mulder2 {:given-name "fox"  :surname "mulder"}
    :scully  {:given-name "Dana" :surname "Scully"}
    :scully2 {:given-name "Dan"  :surname "Scully"}})


(defn -main []
  ; true
  (println
    (records-match? [:given-name :surname] (data :mulder) (data :molder)))

  ; true
  (println
    (records-match? [:given-name :surname] (data :mulder) (data :mulder2)))

  ; true
  (println
    (records-match? [:given-name :surname] (data :scully) (data :scully2)))

  ; false
  (println
    (records-match? [:given-name :surname] (data :mulder) (data :scully))))
