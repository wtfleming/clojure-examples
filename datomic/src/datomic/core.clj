(ns datomic.core
  (:require [datomic.client.api :as d]))

;; To work with this in a REPL, ensure you call (setup-database) prior to running any queries
;; That will ensure the database and schema were created, and data was added.

(def client (d/client {:server-type :dev-local
                       :storage-dir :mem    ;; using in memory storage, data will be erased when the process ends
                       :system "examples"}))

(def db-name "example-db")

(defn get-db
  "Helper function to get a database"
  []
  (let [conn (d/connect client {:db-name db-name})
        db (d/db conn)]
    db))

(def book-schema
  "Schema for books"
  [{:db/ident :book/title
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "The title of the book"}

   {:db/ident :book/authors
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/many
    :db/doc "The authors of the book"}

   {:db/ident :book/release-year
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one
    :db/doc "The year the book was published"}])

(def first-books
  "Books to be added to Datomic"
  [{:book/title "The Joy of Clojure"
    :book/authors ["Michael Fogus" "Chris Houser"]
    :book/release-year 2014}
   {:book/title "Elements of Clojure"
    :book/authors ["Zachary Tellman"]
    :book/release-year 2019}
   {:book/title "Getting Clojure"
    :book/authors ["Russ Olsen"]
    :book/release-year 2018}])


(defn setup-database
  "Creates a database, then adds the schema and some data to it."
  []
  (d/create-database client {:db-name db-name})
  (let [conn (d/connect client {:db-name db-name})]
    (d/transact conn {:tx-data book-schema}) ;; Create the schema
    (d/transact conn {:tx-data first-books}) ;; Insert some data
))


;; ----- Queries -----
(def all-titles-q
  "Query to find all book titles"
  '[:find ?book-title
    :where [_ :book/title ?book-title]])

(def published-after-2017-q
  "Query to find the title and authors of all books published after 2017"
  '[:find ?title ?authors
    :where
    [?book :book/release-year ?release-year]
    [?book :book/title ?title]
    [?book :book/authors ?authors]
    [(< 2017 ?release-year)]])

(def book-titles-by-author-q
  "Query to find all books by an author"
  '[:find ?title
    :in $ ?author-name
    :where [?book :book/authors ?author-name]
    [?book :book/title ?title]])


;; Queries I run in CIDER
;; Ensure (setup-database) has already been called
(quote
 (do
   (d/q all-titles-q (get-db))
   (d/q published-after-2017-q (get-db))
   (d/q book-titles-by-author-q (get-db) "Michael Fogus")))
