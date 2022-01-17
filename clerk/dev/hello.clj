;; Hello Clerk
;;
;; See https://www.youtube.com/watch?v=3ANS2NTNgig
(ns hello
  (:require [clojure.string :as str]
            [nextjournal.clerk :as clerk]))

(+ 1 2)

(def letter->words
  (->> (slurp "/usr/share/dict/words")
       str/split-lines
       (group-by (comp keyword str/lower-case str first))
       (into (sorted-map))))

(clerk/table letter->words)

(clerk/plotly {:data [{:x (keys letter->words)
                       :y (map count (vals letter->words))}]})
