(ns apache-commons-daemon.core
  (:require [com.stuartsierra.component :as component])
  (:import [org.apache.commons.daemon Daemon DaemonContext])
  (:gen-class
   :implements [org.apache.commons.daemon.Daemon]))


;; Application Metrics Component
(defrecord MetricsComponent [num-ticks]
  ;; Implement the Lifecycle protocol
  component/Lifecycle

  (start [component]
    (println ";; Starting MetricsComponent")
    (reset! (:num-ticks component) 0)
    component)

  (stop [component]
    (println ";; Stopping MetricsComponent")
    (println (str ";; Metric num ticks: " @(:num-ticks component)))
    component))

(defn make-metrics-component
  "Constructor for a metrics component"
  []
  (map->MetricsComponent {:num-ticks (atom 0)}))

(defn inc-num-ticks-metric [metrics-component]
  (swap! (get metrics-component :num-ticks) inc))


;; Tick Component
(defn do-ticks
  "Print to stdout and increment the ticks metric once a second when the component is running"
  [state metrics-component]
  (while (= :running @state)
    (println "tick")
    (inc-num-ticks-metric metrics-component)
    (Thread/sleep 1000)))

(defrecord TickComponent [state metrics-component]
  ;; Implement the Lifecycle protocol
  component/Lifecycle

  (start [component]
    (println ";; Starting TickComponent")
    (reset! (:state component) :running)
    ;; Do some work in another thread
    (future (do-ticks (:state component) metrics-component))
    component)

  (stop [component]
    (println ";; Stopping TickComponent")
    (reset! (:state component) :stopped)
    component))


(defn make-tick-component
  "Constructor for a tick component"
  []
  (map->TickComponent {:state (atom :stopped)}))


;; Application System
(defn make-app-system []
  (component/system-map
   :metrics-component (make-metrics-component)
   :tick-component (component/using
                    (make-tick-component)
                    [:metrics-component])))

(def app-system (make-app-system))

;; Separate start/stop functions for easier development in a REPL
(defn start []
  (alter-var-root #'app-system component/start))

(defn stop []
  (alter-var-root #'app-system component/stop))


;; Commons Daemon implementatation
(defn -init [this ^DaemonContext context])

(defn -start [this]
  (start))

(defn -stop [this]
  (stop))

(defn -destroy [this])

