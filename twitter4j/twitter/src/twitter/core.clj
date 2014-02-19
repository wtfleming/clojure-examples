(ns twitter.core
  (:gen-class)
  (:import [twitter4j TwitterFactory Query QueryResult]
           [twitter4j.auth AccessToken]))


(defn- formatStatusesForOutput [statuses]
  (map #(str (.getScreenName (.getUser %)) " : " (.getText %)) statuses))


(defn- getOAuthAuthorizedTwitter []
  (let [access-token-public "YOUR_ACCESS_TOKEN"
        access-token-secret "YOUR_ACCESS_TOKEN_SECRET"
        access-token (AccessToken. access-token-public access-token-secret)
        consumer-key "YOUR_CONSUMER_KEY"
        consumer-secret "YOUR_CONSUMER_SECRET"
        twitter (.getInstance (TwitterFactory.))]
    (doto twitter
      (.setOAuthConsumer consumer-key consumer-secret)
      (.setOAuthAccessToken access-token))
    twitter))


(defn search [query-string]
  (let [query (Query. query-string)]
    (.setCount query 20)
    (-> (getOAuthAuthorizedTwitter)
      (.search query)
      (.getTweets))))

(defn getHomeTimeline []
  (-> (getOAuthAuthorizedTwitter)
    (.timelines)
    (.getHomeTimeline)))



(defn -main []
  (def results (search "clojure"))
;  (def results (getHomeTimeline))
  (def output (formatStatusesForOutput results))
  (doseq [tweet output] (println tweet)))
