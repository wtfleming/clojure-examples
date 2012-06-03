(defproject web-compojure "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [compojure "1.0.4"]
                 [hiccup "1.0.0"]
                 [cheshire "4.0.0"]]
  :dev-dependencies [[lein-ring "0.7.0"]]
  :ring {:handler web-compojure.core/app})
