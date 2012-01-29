(ns lucene.core
  (:gen-class))
 
(import org.apache.lucene.analysis.standard.StandardAnalyzer) 
(import '(org.apache.lucene.document Document Field Field$Store Field$Index))
(import '(org.apache.lucene.index IndexWriter IndexWriterConfig IndexReader))
(import org.apache.lucene.queryParser.QueryParser)
(import '(org.apache.lucene.search Searcher IndexSearcher Query TopDocs))
(import org.apache.lucene.store.RAMDirectory)
(import org.apache.lucene.util.Version)


(def version (Version/LUCENE_35))

(defn createDocument [title content]
  (def doc (new Document))
  (def titleField (new Field "title" title Field$Store/YES Field$Index/NO))
  (def contentField (new Field "content" content Field$Store/YES Field$Index/ANALYZED ))
  (.add doc titleField)
  (.add doc contentField)
  doc)


(defn search [searcher queryString]
  (def queryParser (new QueryParser version "content" (new StandardAnalyzer version)))
  (def query (.parse queryParser queryString))
  (def topDocs (.search searcher query 5))
  (println "--------------------------")
  (println (str "Query: " queryString))
  (println (str "Num hits: " (.totalHits topDocs)))
  (doseq [document (.scoreDocs topDocs)]
    (print (str (.score document) " "))
    (println (.get (.doc searcher (.doc document)) "content")))
  (println "--------------------------"))



(defn -main []
  (def index (new RAMDirectory))
  (def analyzer (new StandardAnalyzer version))
  (def config (new IndexWriterConfig version analyzer))
  (def writer (new IndexWriter index config))
  
  (.addDocument writer (createDocument " E. W. Dijkstra" "The computing scientist's main challenge is not to get confused by the complexities of his own making."))
  (.addDocument writer (createDocument "Brian Kernigan" "Controlling complexity is the essence of computer programming."))
  (.addDocument writer (createDocument "David Gelernter" "Beauty is more important in computing than anywhere else in technology because software is so complicated. Beauty is the ultimate defence against complexity."))
  
  (.optimize writer)
  (.close writer)

  (def searcher (new IndexSearcher (IndexReader/open index)))
  (search searcher "complexity")
  (search searcher "main")
  (.close searcher))
