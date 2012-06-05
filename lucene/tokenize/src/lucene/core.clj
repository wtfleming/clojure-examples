(ns lucene.core
  (:gen-class)
  (import [org.apache.lucene.analysis TokenStream]
          [org.apache.lucene.analysis.tokenattributes CharTermAttribute]
          [org.apache.lucene.analysis.standard StandardAnalyzer]
          [org.apache.lucene.util Version]))


(def LUCENE_VERSION (Version/LUCENE_36))
(def STOP_WORDS StandardAnalyzer/STOP_WORDS_SET)
(def TEXT_TO_TOKENIZE "The Quick quick brown fox jumps over the lazy dog.")


(defn tokenizer-seq
  "Build a lazy-seq out of a tokenizer with CharTermAttribute"
  [^TokenStream tokenizer ^CharTermAttribute term-att]
  (lazy-seq
    (when (.incrementToken tokenizer)
      (cons (.term term-att) (tokenizer-seq tokenizer term-att)))))


(defn tokenize-text
  "Apply a lucene tokenizer to cleaned text content as a lazy-seq"
  [^StandardAnalyzer analyzer text]
  (let [reader (java.io.StringReader. text)
        tokenizer (.tokenStream analyzer nil reader)
        term-att (.addAttribute tokenizer CharTermAttribute)]
    (tokenizer-seq tokenizer term-att)))


(defn -main []
  (def analyzer (StandardAnalyzer. LUCENE_VERSION STOP_WORDS))
  (def tokens (tokenize-text analyzer TEXT_TO_TOKENIZE))

  (println TEXT_TO_TOKENIZE)
  (doseq [token tokens] (println token)))


