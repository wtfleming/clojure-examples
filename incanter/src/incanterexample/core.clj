(ns incanterexample.core
  (:gen-class)
  (:use (incanter core stats charts)))


(defn -main []
  (view (histogram (sample-normal 1000))))
