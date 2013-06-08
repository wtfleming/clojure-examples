(defproject web-compojure "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.5"]
                 [hiccup "1.0.0"]
                 [cheshire "4.0.0"]]
  :dev-dependencies [[ring/ring-devel "1.1.0"]]
  :plugins [[lein-ring "0.8.3"]]
  :ring {:handler web-compojure.core/app})
