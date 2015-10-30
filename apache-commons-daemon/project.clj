(defproject apache-commons-daemon "0.1.0-SNAPSHOT"
  :description "Example of running a daemon"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [commons-daemon/commons-daemon "1.0.15"]
                 [com.stuartsierra/component "0.3.0"]]
  :main apache-commons-daemon.core
  :aot :all)
