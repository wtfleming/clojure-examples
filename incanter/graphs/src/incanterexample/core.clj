(ns incanterexample.core
  (:require
    [incanter.charts :as c]
    [incanter.core :as i]
    [incanter.io :as iio]
    [incanter.latex :as latex]
    [incanter.stats :as s]
    [incanter.datasets])
  (:import
    org.jfree.chart.renderer.category.LayeredBarRenderer
    org.jfree.util.SortOrder))

;; Can use :jvm-opts ["-Djava.awt.headless=true"]
;; in project.clj to not display a window

(defn scatter-plot []
  (let [iris (incanter.datasets/get-dataset :iris)
        iris-petal-scatter (c/scatter-plot
                             (i/sel iris :cols :Petal.Width)
                             (i/sel iris :cols :Petal.Length)
                             :title "Irises: Petal Width by Petal Length"
                             :x-label "Width (cm)"
                             :y-label "Length (cm)")]
    (i/view iris-petal-scatter)))


(defn bar-chart []
  (let [chick-weight (incanter.datasets/get-dataset :chick-weight)
        chick-weight-bar (i/with-data
                           (i/$order :Diet :asc
                             (i/$rollup :sum :weight :Diet chick-weight))
                           (c/bar-chart
                             (i/$map int :Diet)
                             :weight
                             :title "Chick Weight"
                             :x-label "Diet"
                             :y-label "Weight"))]
    (i/view chick-weight-bar)))

(defn bar-chart-non-numeric-data
  "Display a chart showing how many items are in each category"
  []
  (let [shrooms (iio/read-dataset "data/agaricus-lepiota.data" :header true)
        shroom-cap-bar (i/with-data
                         (->> shrooms
                           (i/$group-by :cap-shape)
                           (map (fn [[k v]] (assoc k :count (i/nrow v))))
                           (sort-by :cap-shape)
                           i/to-dataset)
                         (c/bar-chart :cap-shape :count))]
    (i/view shroom-cap-bar)))

(defn histogram []
  (let [iris (incanter.datasets/get-dataset :iris)
        iris-petal-length-hist (c/histogram
                                 (i/sel iris :cols :Petal.Length)
                                 :title "Iris Petal Lengths"
                                 :x-label "cm"
                                 :nbins 20)]
    (i/view iris-petal-length-hist)))

(defn function-plot
  []
  (let [f-plot (c/function-plot
                 #(i/sin %)
                 0.0
                 10.0
                 :title "Sine function"
                 :y-label "Sine")]
    (i/view f-plot)))


(defn function-plot-with-latex
  []
  (let [f-plot (c/function-plot
                 #(i/sin %)
                 0.0
                 10.0
                 :title "Sine function"
                 :y-label "Sine")
        equation "f(x)={\\sin x}"]
    (latex/add-latex-subtitle f-plot equation)
    (i/view f-plot)))

(defn scatter-chart-with-lines
  "Scatter chart with a linear regression shown"
  []
  (let [iris (incanter.datasets/get-dataset :iris)
        iris-petal-scatter (c/scatter-plot
                             (i/sel iris :cols :Petal.Width)
                             (i/sel iris :cols :Petal.Length)
                             :title "Irises: Petal Width by Petal Length"
                             :x-label "Width (cm)"
                             :y-label "Length (cm)")
        iris-petal-lm (s/linear-model
                        (i/sel iris :cols :Petal.Length)
                        (i/sel iris :cols :Petal.Width)
                      :intercept false)]
    (c/add-lines
      iris-petal-scatter
      (i/sel iris :cols :Petal.Width)
      (:fitted iris-petal-lm)
      :series-label "Linear Relationship")
    (i/view iris-petal-scatter)))

(defn customizing-with-jfreechart []
  (let [iris (incanter.datasets/get-dataset :iris)
        iris-dimensions (i/with-data
                          iris
                          (doto (c/bar-chart
                                  :Species
                                  :Petal.Width
                                  :title "iris dimensions"
                                  :x-label "species"
                                  :y-label "cm"
                                  :series-label "petal width"
                                  :legend true)
                            (c/add-categories
                              :Species :Sepal.Width
                              :series-label "sepal width")
                            (c/add-categories
                              :Species :Petal.Length
                              :series-label "petal length")
                            (c/add-categories
                              :Species :Sepal.Length
                              :series-label "sepal length")))]
    (doto (.getPlot iris-dimensions)
      (.setRenderer (doto (LayeredBarRenderer.)
                      (.setDrawBarOutline false)))
      (.setRowRenderingOrder SortOrder/DESCENDING))
    (i/view iris-dimensions)))

(defn save-graph-to-png []
  (let [iris (incanter.datasets/get-dataset :iris)
        iris-petal-scatter (c/scatter-plot
                             (i/sel iris :cols :Petal.Width)
                             (i/sel iris :cols :Petal.Length)
                             :title "Irises: Petal Width by Petal Length"
                             :x-label "Width (cm)"
                             :y-label "Length (cm)")]
  (i/save iris-petal-scatter "iris-petal-scatter.png")))


(defn graph-multidimensional-data-with-pca []
  (let [race-data (iio/read-dataset "data/all_160.P3.csv" :header true)
        fields [:P003002 :P003003 :P003004 :P003005 :P003006 :P003007 :P003008]
        race-by-state (reduce #(i/$join [:STATE :STATE] %1 %2)
                        (map #(i/$rollup :sum % :STATE race-data)
                          fields))
        race-by-state-matrix (i/to-matrix race-by-state)
        x (i/sel race-by-state-matrix :cols (range 1 8))
        pca (s/principal-components x)
        components (:rotation pca)
        pc1 (i/sel components :cols 0)
        pc2 (i/sel components :cols 1)
        x1 (i/mmult x pc1)
        x2 (i/mmult x pc2)
        pca-plot (c/scatter-plot
                   x1 x2
                   :x-label "PC1"
                   :y-label "PC2"
                   :title "Census Race Data by State")]
  (i/view pca-plot)))


(defn -main []
  ;(scatter-plot)
  ;(bar-chart)
  ;(bar-chart-non-numeric-data)
  ;(histogram)
  ;(function-plot)
  ;(function-plot-with-latex)
  ;(scatter-chart-lines-with)
  ;(customizing-with-jfreechart)
  ;(save-graph-to-png)
  (graph-multidimensional-data-with-pca)
)


