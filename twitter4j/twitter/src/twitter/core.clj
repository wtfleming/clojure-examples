(ns twitter.core
  (:gen-class)
  (:import [twitter4j TwitterFactory Query QueryResult]
           [twitter4j.auth AccessToken]))


(defn- format-statuses-for-output [statuses]
  (map #(str (.getScreenName (.getUser %)) " : " (.getText %)) statuses))

(defn- get-oauth-authorized-twitter []
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
    (-> (get-oauth-authorized-twitter)
        (.search query)
        (.getTweets))))

(defn get-home-timeline []
  (-> (get-oauth-authorized-twitter)
      (.timelines)
      (.getHomeTimeline)))


(defn -main []
  (def results (search "clojure"))
  ;;  (def results (get-home-timeline))
  (def output (format-statuses-for-output results))
  (doseq [tweet output] (println tweet)))
