(ns apache-commons-exec.core
  (:require [clj-commons-exec :as exec]))

(defn print-date!
  "Runs the date command and prints results to stdout"
  []
  (let [result-promise (exec/sh ["date"])]
    (prn @result-promise))) ;; {:exit 0, :out "Fri Jul 16 18:38:53 EDT 2021\n", :err nil}

(defn pipe-commands!
  "Pipes the result of a command to another, then prints results to stdout"
  []
  (let [results-promises-seq (exec/sh-pipe ["cat"] ["wc" "-w"] {:in "hello world"})]
    (doseq [result-promise results-promises-seq]
      (prn @result-promise))))
;; {:exit 0, :out nil, :err nil}
;; {:exit 0, :out "       2\n", :err nil}

(defn environment-variables!
  "Demonstrates overriding env variables"
  []
  (prn @(exec/sh ["printenv" "HOME"]))                                ;; {:exit 0, :out "/Users/will\n", :err nil}
  (prn @(exec/sh ["printenv" "HOME"] {:env {"HOME" "/Users/rich"}}))) ;; {:exit 0, :out "/Users/rich\n", :err nil}
