(ns flambo-gaming-stack-exchange.etl-users
  (:require [flambo.conf :as conf]
            [flambo.api :as f]
            [flambo.sql :as sql]
            [clojure.data.xml :as xml])
  (:import [org.apache.spark.sql RowFactory]
           [org.apache.spark.sql.types StructType StructField Metadata DataTypes])
  (:gen-class))



(defn build-sql-context
  "Returns a Spark SQLContext"
  [app-name]
  (let [c (-> (conf/spark-conf)
              (conf/master "local[*]")
              (conf/app-name app-name))
        sc (f/spark-context c)]
    (sql/sql-context sc)))


(defn xml->row
  "Parse a row of user xml and return it as a Spark Row"
  [user-xml]
  (let [user (xml/parse-str user-xml)
        {{:keys [Id DisplayName Reputation]} :attrs} user]
    [(RowFactory/create (into-array Object [(Integer/parseInt Id) DisplayName  (Integer/parseInt Reputation)]))]))

"Spark function that reads in a line of XML and potentially returns a Row"
(f/defsparkfn parse-user
  [user-xml]
  (if (.startsWith user-xml  "  <row")
    (xml->row user-xml)
    []))


(def user-schema
  (StructType.
   (into-array StructField [(StructField. "id" (DataTypes/IntegerType) true (Metadata/empty))
                            (StructField. "name" (DataTypes/StringType) true (Metadata/empty))
                            (StructField. "reputation" (DataTypes/IntegerType) true (Metadata/empty))])))


(defn -main [& args]
  (let [home (java.lang.System/getenv "HOME")
        sql-ctx (build-sql-context "ETL Users")
        sc (sql/spark-context sql-ctx)
        xml-users (f/text-file sc (str home "/data/gaming-stackexchange/Users.xml"))
        users (f/flat-map xml-users parse-user)
        users-df (.createDataFrame sql-ctx users user-schema)]
    (.saveAsParquetFile users-df (str home "/data/gaming-stack-exchange-warehouse/users.parquet"))))
