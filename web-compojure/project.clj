(defproject web-compojure "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [compojure "1.0.1"]
                 [hiccup "0.3.8"]
                 [cheshire "2.1.0"]]
  :dev-dependencies [[lein-ring "0.5.4"]]
  :ring {:handler web-compojure.core/app})
