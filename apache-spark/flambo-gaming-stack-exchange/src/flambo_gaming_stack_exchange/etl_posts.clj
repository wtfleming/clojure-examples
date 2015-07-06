(ns flambo-gaming-stack-exchange.etl-posts
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

(defn parse-int
  "Reads a string, returns a number or nil if the string was x"
  [x]
  (if (nil? x)
    x
    (Integer/parseInt x)))

(defn xml->row
  "Parse a row of post xml and return it as a Spark Row"
  [post-xml]
  (let [user (xml/parse-str post-xml)
        {{:keys [OwnerUserId PostTypeId Tags]} :attrs} user]
    [(RowFactory/create (into-array Object [(parse-int OwnerUserId) (parse-int PostTypeId) Tags]))]))


"Spark function that reads in a line of XML and potentially returns a Row"
(f/defsparkfn parse-post
  [post-xml]
  (if (.startsWith post-xml  "  <row")
    (xml->row post-xml)
    []))



(def post-schema
  (StructType.
   (into-array StructField [(StructField. "ownerId" (DataTypes/IntegerType) true (Metadata/empty))
                            (StructField. "postType" (DataTypes/IntegerType) true (Metadata/empty))
                            (StructField. "tags" (DataTypes/StringType) true (Metadata/empty))])))


(defn -main [& args]
  (let [home (java.lang.System/getenv "HOME")
        sql-ctx (build-sql-context "ETL Posts")
        sc (sql/spark-context sql-ctx)
        xml-users (f/text-file sc (str home "/data/gaming-stackexchange/Posts.xml"))
        posts (f/flat-map xml-users parse-post)
        users-df (.createDataFrame sql-ctx posts post-schema)]
    (.saveAsParquetFile users-df (str home "/data/gaming-stack-exchange-warehouse/posts.parquet"))))

