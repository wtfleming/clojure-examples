(ns k-means.core
  (:require [clojure.string :as str])
  (:import
    [weka.core EuclideanDistance]
    [weka.core.converters ArffLoader]
    [weka.clusterers SimpleKMeans]
    [weka.filters Filter]
    [weka.filters.unsupervised.attribute Remove]
    [java.io File]))

(defn random-seed [seed]
  (if (nil? seed)
    (.intValue (.getTime (java.util.Date.)))
    seed))


(defn ->options
  [& opts]
  (into-array
    String
    (map str (flatten (remove nil? opts)))))



(defn analysis-parameter
  [parameter]
  (condp = (count parameter)
    ;; [option-string variable-name default-value]
    ;; ["-N" k 2]
    3 `[~(first parameter) ~(second parameter)]

    ;; [option-string variable-name default-value flag]
    ;; ["-V" verbose false :flag-true]
    4 (condp = (last parameter)
        :flag-true `[(when ~(second parameter)
                       ~(first parameter))]
        :flag-false `[(when-not ~(second parameter)
                        ~(first parameter))]
        :not-nil `[(when-not (nil? ~(second parameter))
                     [~(first parameter) ~(second parameter)])]
        :seq (let [name (second parameter)]
               (apply concat
                      (map-indexed (fn [i flag] `[~flag (nth ~name ~i)])
                                   (first parameter))))

        `[~(first parameter)
          (~(last parameter) ~(second parameter))])

    ;; [option-string variable-name default-value flag option]
    ;; ["-B" distance-of :node-length :flag-equal :branch-length]
    5 (condp = (nth parameter 3)
        :flag-equal `[(when (= ~(second parameter) ~(last parameter))
                        ~(first parameter))]

        :predicate `[(when ~(last parameter)
                       [~(first parameter) ~(second parameter)])])))


(defmacro defanalysis
  ([a-name a-class a-method parameters]
   `(defn ~a-name
      [dataset# &
       ;; The variable-names and default-values are used here
       ;; to build the function's parameter list.
       {:keys ~(mapv second parameters)
        :or ~(into {}
                   (map #(vector (second %) (nth % 2))
                        parameters))}]
      ;; The options, flags, and predicats are used to
      ;; construct the options list.
      (let [options# (->options ~@(mapcat analysis-parameter
                                          parameters))]
        ;; The algorithm's class and invocation function
        ;; are used here to actually perform the
        ;; processing.
        (doto (new ~a-class)
          (.setOptions options#)
          (. ~a-method dataset#))))))


(defanalysis
  k-means SimpleKMeans buildClusterer
  [["-N" k 2]
   ["-I" max-iterations 100]
   ["-V" verbose false :flag-true]
   ["-S" seed 1 random-seed]
   ["-A" distance EuclideanDistance .getName]])


(defn load-arff
  ([filename]
   (.getDataSet (doto (ArffLoader.)
                  (.setFile (File. filename))))))


(defn attr-n
  [instances attr-name]
  (->> instances
    (.numAttributes)
    range
    (map #(vector % (.. instances (attribute %) name)))
    (filter #(= (second %) (name attr-name)))
    ffirst))


(defn filter-attributes
  ([dataset remove-attrs]
   (let [attrs (map inc (map (partial attr-n dataset) remove-attrs))
         options (->options "-R"
                            (str/join \, (map str attrs)))
         rm (doto (Remove.)
              (.setOptions options)
              (.setInputFormat dataset))]
     (Filter/useFilter dataset rm))))



(defn -main []
  (let [iris (load-arff "../data/iris.arff")
        iris-petal (filter-attributes iris [:sepallength :sepalwidth :class])
        km (k-means iris-petal :k 3)]
    (println km)
    (println)
    (println (type km))
    (println (.getSquaredError km))))
