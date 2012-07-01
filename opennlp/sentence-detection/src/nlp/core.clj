(ns nlp.core
  (:import (java.io FileInputStream))
  (:import (opennlp.tools.sentdetect SentenceModel
                                     SentenceDetectorME)))
  

(def text "The history of NLP generally starts in the 1950s, although work can be found from earlier periods. In 1950, Alan Turing published his famous article \"Computing Machinery and Intelligence\" which proposed what is now called the Turing test as a criterion of intelligence. This criterion depends on the ability of a computer program to impersonate a human in a real-time written conversation with a human judge, sufficiently well that the judge is unable to distinguish reliably — on the basis of the conversational content alone — between the program and a real human. The Georgetown experiment in 1954 involved fully automatic translation of more than sixty Russian sentences into English. The authors claimed that within three or five years, machine translation would be a solved problem. However, real progress was much slower, and after the ALPAC report in 1966, which found that ten years long research had failed to fulfill the expectations, funding for machine translation was dramatically reduced. Little further research in machine translation was conducted until the late 1980s, when the first statistical machine translation systems were developed.")

  
(defn -main []
  (with-open [is (FileInputStream. "models/en-sent.bin")]
    (let [model (SentenceModel. is)
          sdetector (SentenceDetectorME. model)
          sentences (.sentDetect sdetector text)] 
      (doseq [s sentences] (println s)))))

