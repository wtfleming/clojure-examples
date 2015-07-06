(defproject flambo-gaming-stack-exchange "0.1.0-SNAPSHOT"
  :description "Example of using Spark and Flambo"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :profiles {:uberjar {:aot :all}
             :provided {:dependencies
                        [[org.apache.spark/spark-core_2.10 "1.3.1"]]}}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [yieldbot/flambo "0.6.0"]
                 [commons-codec/commons-codec "1.10"]
                 [org.clojure/data.xml "0.0.8"]
                 [org.apache.spark/spark-sql_2.10 "1.3.1"]])
