(ns recommender.core
  (:import
      [java.io File]
      [org.apache.mahout.cf.taste.impl.eval
        AverageAbsoluteDifferenceRecommenderEvaluator RMSRecommenderEvaluator
        GenericRecommenderIRStatsEvaluator]
      [org.apache.mahout.cf.taste.eval RecommenderBuilder RecommenderIRStatsEvaluator]
      [org.apache.mahout.cf.taste.impl.model.file FileDataModel]
      [org.apache.mahout.cf.taste.impl.neighborhood NearestNUserNeighborhood]
      [org.apache.mahout.cf.taste.impl.recommender GenericUserBasedRecommender]
      [org.apache.mahout.cf.taste.impl.similarity PearsonCorrelationSimilarity]
      [org.apache.mahout.common RandomUtils]
      ))

(defn recommend [file]
  (let [model (FileDataModel. (File. file)) 
       similarity (PearsonCorrelationSimilarity. model) 
       neighborhood (NearestNUserNeighborhood. 2 similarity model) 
       recommender  (GenericUserBasedRecommender. model neighborhood 
                                                  similarity)]
    (.recommend recommender 1 1)))
 
 
 (defn evaluator [file]
  (let [_ (RandomUtils/useTestSeed)
        file-model (FileDataModel. (File. file))
        evaluator (AverageAbsoluteDifferenceRecommenderEvaluator.)
        ;; evaluator (RMSRecommenderEvaluator.)
        builder (reify RecommenderBuilder
                  (buildRecommender [_this model]
                    (let [similarity (PearsonCorrelationSimilarity. model) 
                          neighborhood (NearestNUserNeighborhood. 2 similarity model)
                          recommender  (GenericUserBasedRecommender. model
                                                                     neighborhood
                                                                     similarity)]
                                              recommender)))] 
    (.evaluate evaluator builder nil file-model 0.7 1.0)))


(defn precision [file]
  (let [_ (RandomUtils/useTestSeed)
        file-model (FileDataModel. (File. file))
        evaluator (GenericRecommenderIRStatsEvaluator.)
        builder (reify RecommenderBuilder
                  (buildRecommender [_this model]
                    (let [similarity (PearsonCorrelationSimilarity. model) 
                          neighborhood (NearestNUserNeighborhood. 2 similarity model)
                          recommender  (GenericUserBasedRecommender. model
                                                                     neighborhood
                                                                     similarity)]
                      recommender)))] 
    (.evaluate evaluator builder nil file-model nil 2
               GenericRecommenderIRStatsEvaluator/CHOOSE_THRESHOLD
               1.0)))
    
    

(defn -main []
  (def items (recommend "resources/intro.csv"))
  (def stats (precision "resources/intro.csv"))

  (doseq [i items] (println (str "Recommended item: " (.getItemID i) " Value: " (.getValue i))))
  (println (str "Evaluation: " (evaluator "resources/intro.csv")))
  (println (str "Precision: " (.getPrecision stats) " Recall: " (.getRecall stats))))
    
  
