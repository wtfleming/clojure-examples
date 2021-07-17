(ns tika.core
  (:gen-class)
  (:import [org.apache.tika.language LanguageIdentifier]))


(defn identify-language [content]
  (let [li (LanguageIdentifier. content)]

    (println "------------------------------")
    (println content)
    (print "Language: ")
    (println (.getLanguage li))
    (print "Is reasonably certain: ")
    (println (.isReasonablyCertain li))
))


(defn -main []
  
  ; Not that many supported languages by tika
  (println (LanguageIdentifier/getSupportedLanguages))


  (identify-language "I am happy to join with you today in what will go down in history as the greatest demonstration for freedom in the history of our nation.")

  (identify-language "Ich freue mich, mit Ihnen gemeinsam heute in dem, was wird in die Geschichte eingehen als die größte Demonstration für Freiheit in der Geschichte unserer Nation.")

  (identify-language "Estoy orgulloso de reunirme con ustedes hoy en lo que va a pasar a la historia como la mayor manifestación por la libertad en la historia de nuestra nación."))
