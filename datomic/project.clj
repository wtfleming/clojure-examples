(defproject datomic "0.1.0-SNAPSHOT"
  :description "Examples of working with Datomic"
  :repositories [["cognitect-dev-tools" {:url      "https://dev-tools.cognitect.com/maven/releases/"
                                         :datomic-username :env
                                         :datomic-password :env}]]
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[com.datomic/dev-local "0.9.232"]
                 [org.clojure/clojure "1.10.3"]]
  :repl-options {:init-ns datomic.core})
