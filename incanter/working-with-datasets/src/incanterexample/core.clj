(ns incanterexample.core
  (:require
    [clojure.data.csv :as csv]
    [clojure.data.json :as json]
    [clojure.java.io :as java-io]
    [clojure.set :as clj-set])
  (:use [incanter core io stats charts datasets]))


(defn load-incanter-dataset []
  (let [iris (get-dataset :iris)]
    (println (col-names iris))
    ; [:Sepal.Length :Sepal.Width :Petal.Length :Petal.Width :Species]

    (println (nrow iris)) ; 150
    (println (set ($ :Species iris))) ; {versicolor virginica setosa}
))


(defn load-clojure-data-structure-into-dataset []
  (let [matrix-set   (to-dataset [[1 2 3] [4 5 6]])
        map-set      (to-dataset {:a 1 :b 2 :c 3})
        maps-set     (to-dataset [{:a 1 :b 2 :c 3} {:a 4 :b 5 :c 6}])
        matrix-set-2 (dataset [:a :b :c] [[1 2 3] [4 5 6]])]

    (println (nrow matrix-set))        ; 2
    (println (col-names matrix-set))   ; [:col-0 :col-1 :col-2]

    (println (nrow map-set))           ; 1
    (println (col-names map-set))      ; [:a :b :c]

    (println (nrow maps-set))          ; 2
    (println (col-names maps-set))     ; [:a :c :b]

    (println (nrow matrix-set-2))      ; 2
    (println (col-names matrix-set-2)) ; [:a :b :c]
    ))


(defn view-dataset []
  (let [iris (get-dataset :iris)]
    (view iris)))


(defn convert-dataset-to-matrices []
  (let [data-file "data/all_160_in_51.P35.csv"
        va-data   (read-dataset data-file :header true)
        va-matrix (to-matrix ($ [:POP100 :HU100 :P035001] va-data))]

    ; We can treat the matrix as a clojure seq
    (println (first va-matrix))
    (println (take 2 va-matrix))
    (println (count va-matrix))

    ; Get sum of each column in the matrix
    (println (reduce plus va-matrix))))


(defn using-infix-formulas []
  (let [data-file "data/all_160_in_51.P35.csv"
        va-data   (read-dataset data-file :header true)
        va-matrix (to-matrix ($ [:POP100 :HU100 :P035001] va-data))]
    (println ($= 7 * 4))     ; 28
    (println ($= 7 * 4 + 3)) ; 31

    (println ($= va-matrix * 4))
    (println ($= (first va-matrix) * 4))

    ; Take mean of each column
    (println ($= (reduce plus va-matrix) / (count va-matrix)))))

(defn selecting-columns-and-rows []
  (let [data-file "data/all_160.P3.csv"
        race-data (read-dataset data-file :header true)]

    ;; $ is a wraper over incanter's sel function
    ;;  We can use it to select columns
    (println ($ :POP100 race-data))
    (println ($ [:STATE :POP100 :POP100.2000] race-data))

    ;; And select rows
    (println ($ 0 :all race-data)) ; one row
    (println ($ [0 1 2 3] :all race-data)) ; multiple rows

    ;; And select specific columns and rows
    (println ($ 0 [:STATE :POP100] race-data)) ; 1 row, 2 cols
    (println ($ [0 1 2 3] [:STATE :POP100] race-data))))


(defn filtering-with-where []
  (let [data-file "data/all_160_in_51.P35.csv"
        va-data      (read-dataset data-file :header true)
        richmond     ($where {:NAME "Richmond city"} va-data)
        small-towns  ($where {:POP100 {:lte 1000}} va-data)
        medium-towns ($where {:POP100 {:gt 1000 :lt 40000}} va-data)
        random-half ($where {:GEOID {:$fn (fn [_] (< (rand) 0.5))}} va-data)
]

    (println richmond) ; city of richmond
    (println small-towns)  ; cities with population less than or equal 1000
    (println (nrow small-towns)) ; 232
    (println ($ [0 1 2] :all small-towns))
    (println medium-towns) ; population greater than 1,000, less than 4,0000
    (println (nrow va-data))
    (println (nrow random-half)) ; random selection of about half the lines
))

(defn grouping-data-with-group-by []
  (let [data-file "data/all_160.P3.csv"
        race-data (read-dataset data-file :header true)
        by-state ($group-by :STATE race-data)]

    (println (take 5 (keys by-state)))
    ; ({:STATE 29} {:STATE 28} {:STATE 31} {:STATE 30} {:STATE 25})

    ; We can get the data for a state out
    (println ($ [0 1 2 3] :all (by-state {:STATE 30}))) ; first 4 rows
))


(defn saving-to-csv-and-json []
  (let [data-file "data/all_160.P3.csv"
         race-data (read-dataset data-file :header true)
         census2010 ($ [:STATE :NAME :POP100 :P003002] race-data)]

    ;; Write to CSV formatted
    (with-open [f-out (java-io/writer "data/census2010.csv")]
      ;; Write column names
      (csv/write-csv f-out [(map name (col-names census2010))])
      ;; Then write data
      (csv/write-csv f-out (to-list census2010)))

    ;; Write to JSON formatted file
    (with-open [f-out (java-io/writer "data/census2010.json")]
      (json/write (:rows census2010) f-out))))



(defn dedup-second [a b id-col]
  (let [a-cols (set (col-names a))]
    (conj (filter #(not (contains? a-cols %)) (col-names b)) id-col)))


(defn joining-multiple-datasets []
  (let [family-data (read-dataset "data/all_160_in_51.P35.csv" :header true)
        racial-data (read-dataset "data/all_160_in_51.P3.csv" :header true)
        racial-short ($ (vec (dedup-second family-data racial-data :GEOID)) racial-data)
        all-data ($join [:GEOID :GEOID] family-data racial-short)]

    ;; #{:SUMLEV :HU100.2000 :HU100 :NAME :GEOID :NECTA :CBSA :CSA :POP100.2000 :CNECTA :POP100 :COUNTY :STATE}
    (println (clj-set/intersection
               (set (col-names family-data))
               (set (col-names racial-data))))

    (println (col-names all-data))

    (println (nrow family-data))  ; 591
    (println (nrow racial-data))  ; 591
    (println (nrow racial-short)) ; 591
    (println (nrow all-data))     ; 591

))



(defn -main []
  (load-incanter-dataset)
  ;(load-clojure-data-structure-into-dataset)
  ;(view-dataset)
  ;(convert-dataset-to-matrices)
  ;(using-infix-formulas)
  ;(selecting-columns-and-rows)
  ;(filtering-with-where)
  ;(grouping-data-with-group-by)
  ;(saving-to-csv-and-json)
  ;(joining-multiple-datasets)
)


