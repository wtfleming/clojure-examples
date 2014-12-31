(defproject cascalog-examples "0.1.0-SNAPSHOT"
  :description "Cascalog example queries"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/data.csv "0.1.2"]
                 [cascalog "2.1.1"]
                 [cheshire "5.4.0"]]
  :profiles { :dev {:dependencies [[org.apache.hadoop/hadoop-core "1.0.4"]]}})
