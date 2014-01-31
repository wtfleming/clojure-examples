(ns spell.core
  (:gen-class)
  (:require [clojure.string :as string])
  (:use  [clojure.set :only [union]]))


(defn tokenize
  [text]
  (re-seq #"[a-z]+" (string/lower-case text)))

(defn train
  [features]
  (frequencies features))

(def n-words
  (train (tokenize (slurp "data/big.txt"))))

(def alphabet "abcdefghijklmnopqrstuvwxyz")


(defn split-word
  "Split a word into 2 parts at position i."
  [word i]
  [(.substring word 0 i)
   (.substring word i)])

(defn delete-char
  "Delete the first character in the second part"
  [[w1 w2]]
  (str w1 (.substring w2 1)))

(defn transpose-split
  "Transpose the first 2 characters of the second part."
  [[w1 w2]]
  (str w1 (second w2) (first w2) (.substring w2 2)))

(defn replace-split
  "Replace the first character of the second part with every letter."
  [[w1 w2]]
  (let [w2-0 (.substring w2 1)]
    (map #(str w1 % w2-0) alphabet)))

(defn insert-split
  "Insert every letter into the word at the split."
  [[w1 w2]]
  (map #(str w1 % w2) alphabet))

(defn edits-1
  [word]
  (let [splits (map (partial split-word word) (range (inc (count word))))
        long-splits (filter #(> (count (second %)) 1) splits)
        deletes (map delete-char long-splits)
        transposes (map transpose-split long-splits)
        replaces (mapcat replace-split long-splits)
        inserts (remove nil? (mapcat insert-split splits))]
    (set (concat deletes transposes replaces inserts))))

(defn known-edits-2
  [word]
  (set (filter (partial contains? n-words)
         (apply union (map #(edits-1 %) (edits-1 word))))))

(defn known
  [words]
  (set (filter (partial contains? n-words) words)))

(defn correct
  [word]
  (let [candidate-thunks [#(known (list word))
                          #(known (edits-1 word))
                          #(known-edits-2 word)
                          #(list word)]]
    (->>
      candidate-thunks
      (map (fn [f] (f)))
    (filter #(> (count %) 0))
    first (map (fn [w] [(get n-words w 1) w]))
    (reduce (partial max-key first))
    second)))

(defn -main []

  ; delete
  (println (correct "deete"))

  ; editor
  (println (correct "editr"))

  ; tranpsose
  ; Unable to correct as transpose is not in the training set
  (println (correct "tranpsose"))

  ; editor
  (println (correct "eidtor"))

  ; elder
  ; Incorrect correction
  (println (correct "eidtr"))

  ;; Why are there mistakes?
  ; nil
  (println (n-words "transpose"))

  ; 40
  (println (n-words "elder"))

  ; 17
  (println (n-words "editor"))

)
