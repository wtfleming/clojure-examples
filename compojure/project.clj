(defproject web-compojure "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [compojure "1.6.2"]
                 [hiccup "1.0.5"]
                 [cheshire "5.10.0"]]
  :dev-dependencies [[ring/ring-devel "1.9.3"]]
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler web-compojure.core/app})
