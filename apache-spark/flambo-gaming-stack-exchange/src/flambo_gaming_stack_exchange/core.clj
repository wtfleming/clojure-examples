(ns flambo-gaming-stack-exchange.core
  (:require [flambo.conf :as conf]
            [flambo.api :as f]
            [flambo.sql :as sql])
  (:import [org.apache.spark.sql Column]
           [org.apache.commons.codec.binary Hex])
  (:gen-class))

(defn string-array
  "Helper function for java interop."
  [& strings]
  (into-array String strings))

(defn column-array
  "Helper function for java interop."
  [& columns]
  (into-array Column columns))

(defn build-sql-context
  "Returns a Spark SQLContext"
  [app-name]
  (let [c (-> (conf/spark-conf)
              (conf/master "local[*]")
              (conf/app-name app-name))
        sc (f/spark-context c)]
    (sql/sql-context sc)))

(defn hash-string
  "Returns a hexidecimal encoded SHA-1 hash of a string"
  [data]
  (-> (java.security.MessageDigest/getInstance "SHA-1")
      (.digest (.getBytes data))
      (Hex/encodeHexString)))


"Hash name in a row with the schema [name reputation], returning a
 new vector with the name hashed."
(f/defsparkfn hash-name
  [row]
  (let [[name reputation] row
        hashed-name (hash-string name)]
    [hashed-name reputation]))


(defn -main [& args]
  (let [home (java.lang.System/getenv "HOME")
        sql-ctx (build-sql-context "Stack Exchange Queries")

        ;; Read in the users Parquet file
        users (sql/parquet-file sql-ctx (string-array (str home "/data/gaming-stack-exchange-warehouse/users.parquet")))

        ;; This is one way to query a DataFrame.
        query (-> users
                  (.select (column-array (.col users "name") (.col users "reputation")))
                  (.filter "reputation > 30000")
                  (.orderBy (column-array (-> users
                                              (.col "reputation")
                                              (.desc))))
                  (.limit 10))

        ;; This is another way to run the same query, but to do it this way
        ;; we must first register any tables we will be using.
        _ (sql/register-temp-table users "users")
        sql-query (sql/sql sql-ctx "SELECT name, reputation FROM users WHERE reputation > 30000 ORDER BY reputation DESC LIMIT 10")

        ;; We can also turn a DataFrame to an RDD and use Flambo functions.
        ;; Here we hash the users name prior to displaying it.
        rdd-query (-> sql-query
                      (.toJavaRDD)
                      (f/map sql/row->vec)
                      (f/map hash-name)
                      (f/foreach (f/fn [x] (println x))))

        ;; Create and register the posts table
        posts (sql/parquet-file sql-ctx (string-array (str home "/data/gaming-stack-exchange-warehouse/posts.parquet")))
        _ (sql/register-temp-table posts "posts")

        ;; Top 10 users by number of questions questions asked about the
        ;; game Dwarf Fortress.
        df-query (sql/sql sql-ctx "SELECT u.name, count(1) as cnt FROM users u, posts p WHERE p.tags LIKE '%dwarf-fortress%' AND u.id = p.ownerId GROUP BY u.name ORDER BY cnt DESC LIMIT 10")]
    (.show sql-query)
    (.show df-query)))
