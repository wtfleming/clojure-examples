(ns lucene.core
  (:gen-class)
  (import [org.apache.lucene.analysis.standard StandardAnalyzer]
          [org.apache.lucene.document Document Field Field$Store Field$Index]
          [org.apache.lucene.index IndexWriter IndexWriterConfig IndexReader]
          [org.apache.lucene.queryParser QueryParser]
          [org.apache.lucene.search Searcher IndexSearcher Query TopDocs]
          [org.apache.lucene.store RAMDirectory]
          [org.apache.lucene.util Version]))


(def LUCENE_VERSION (Version/LUCENE_35))


(defn createDocument [title content]
  (let [document (Document.)
        titleField (Field. "title" title Field$Store/YES Field$Index/NO)
        contentField (Field. "content" content Field$Store/YES Field$Index/ANALYZED)]
    (.add document titleField)
    (.add document contentField)
    document))


(defn search [searcher queryString]
  (let [queryParser (QueryParser. LUCENE_VERSION "content" (StandardAnalyzer. LUCENE_VERSION))
        query (.parse queryParser queryString)
        topDocs (.search searcher query 5)]
    (println "--------------------------")
    (println (str "Query: " queryString))
    (println (str "Num hits: " (.totalHits topDocs)))
    (doseq [document (.scoreDocs topDocs)]
      (print (str (.score document) " "))
      (println (.get (.doc searcher (.doc document)) "content")))
    (println "--------------------------")))


(defn createIndex []
  (let [index (RAMDirectory.)
        analyzer (StandardAnalyzer. LUCENE_VERSION)
        config (IndexWriterConfig. LUCENE_VERSION analyzer)
        writer (IndexWriter. index config)]
    (.addDocument writer (createDocument " E. W. Dijkstra" "The computing scientist's main challenge is not to get confused by the complexities of his own making."))
    (.addDocument writer (createDocument "Brian Kernigan" "Controlling complexity is the essence of computer programming."))
    (.addDocument writer (createDocument "David Gelernter" "Beauty is more important in computing than anywhere else in technology because software is so complicated. Beauty is the ultimate defence against complexity."))
    (.optimize writer)
    (.close writer)
    index))


(defn -main []
  (let [index (createIndex)
        searcher (IndexSearcher. (IndexReader/open index))]   
    (search searcher "complexity")
    (search searcher "main")
    (.close searcher)))

