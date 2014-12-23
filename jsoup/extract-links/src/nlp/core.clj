(ns nlp.core
  (:import (java.io FileInputStream))
  (:import (org.jsoup Jsoup)))

(defn -main []
  (with-open [input (FileInputStream. "en.wikipedia.org.html")]
    (let [doc (Jsoup/parse input "UTF-8" "http://en.wikipedia.org/wiki/Main_Page")
          links (.select doc "a[href]")]
      (doseq [link links]
        (println (.attr link "abs:href"))))))
