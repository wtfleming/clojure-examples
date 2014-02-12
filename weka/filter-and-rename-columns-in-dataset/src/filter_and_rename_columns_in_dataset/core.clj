(ns filter-and-rename-columns-in-dataset.core
  (import
    [weka.core.converters CSVLoader ArffLoader]
    [weka.filters Filter]
    [weka.filters.unsupervised.attribute Remove]
    [java.io File])
  (:require
    [clojure.string :as clj-str]))


(defn ->options
  "Convert options into an array of strings"
  [& opts]
  (into-array String (map str (flatten (remove nil? opts)))))


(defn load-csv
  [filename & {:keys [header]
                 :or {header true}}]
    (let [options (->options (when-not header "-H"))
          loader (doto (CSVLoader.)
                   (.setOptions options)
                   (.setSource (File. filename)))]
      (.getDataSet loader)))


(defn set-fields
  "Take a dataset and a sequence of fields names, rename columns in dataset to field names"
  [instances field-seq]
  (doseq [n (range (.numAttributes instances))]
    (.renameAttribute instances
      (.attribute instances n)
      (name (nth field-seq n)))))

(def fields [
              :geoid :sumlev :state :county :cbsa :csa :necta :cnecta :name :pop100
              :housing-units-100 :pop100-2000 :housing-units-100-2000
              :race-total :race-total-2000
              :race-white :race-white-2000
              :race-black :race-black-2000
              :race-indian :race-indian-2000
              :race-asian :race-asian-2000
              :race-hawaiian :race-hawaiian-2000
              :race-other :race-other-2000
              :race-two-more :race-two-more-2000])


(defn attr-n
  "Take an attribute name and returns the index"
  [instances attr-name]
  (->> instances
    (.numAttributes)
    range
    (map #(vector % (.. instances (attribute %) name)))
    (filter #(= (second %) (name attr-name)))
    ffirst))

(defn delete-attrs
  [instances attr-names]
  (reduce (fn [is n] (.deleteAttributeAt is (attr-n is n)) is)
    instances
    attr-names))


(defn filter-attributes
  [dataset remove-attrs]
  (let [attrs (map inc (map attr-n remove-attrs))
        options (->options "-R"
                  (clj-str/join \, (map str attrs)))
        rm (doto (Remove.)
             (.setOptions options)
             (.setInputFormat dataset))]
    (Filter/useFilter dataset rm)))



(defn -main []
  (let [data (load-csv "../data/all_160.P3.csv" :header true)
        data-fields-set (set-fields data fields)
        data-attrs-removed (delete-attrs data [:sumlev :county :cbsa :csa :necta :cnecta])]

    (prn (take 4 (map #(.. data (attribute %) name) (range (.numAttributes data)))))
    (prn (take 2 data-attrs-removed))))
