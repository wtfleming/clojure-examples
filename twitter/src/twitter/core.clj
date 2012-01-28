(ns twitter.core
  (:gen-class))

(import '(twitter4j TwitterFactory Query QueryResult Tweet))
(import '(twitter4j.auth AccessToken))

; Twitter4j Search returns a list of tweets
(defn formatTweetsForOutput [tweets]
  (map #(str (.getFromUser %) " : " (.getText %)) tweets))


; Other calls return a list of statuses...
(defn formatStatusesForOutput [statuses]
  (map #(str (.getScreenName (.getUser %)) " : " (.getText %)) statuses))


(defn getOAuthAuthorizedTwitter []
  (def accessTokenStr "YOUR_ACCESS_TOKEN")   
  (def accessTokenSecretStr "YOUR_ACCESS_TOKEN_SECRET")  
  (def consumerKey "YOUR_CONSUMER_KEY")
  (def consumerSecret "YOUR_CONSUMER SECRET")
  
  (def accessToken (new AccessToken accessTokenStr accessTokenSecretStr))
  (def twitterFactory (new TwitterFactory))
  (def twitter (.getInstance twitterFactory))
  (.setOAuthConsumer twitter consumerKey consumerSecret)
  (.setOAuthAccessToken twitter accessToken)
  twitter)  
  

(defn search [queryString]
  (def twitter (.getInstance (new TwitterFactory)))
  (def query (new Query queryString))
  (.getTweets (.search twitter query)))
  

(defn getHomeTimeline []
  (def twitter (getOAuthAuthorizedTwitter))
  (.getFriendsTimeline twitter))




(defn -main []
;  (def results (search "clojure"))
;  (def output (formatTweetsForOutput results))
;  (doseq [tweet output] (println tweet))

  (def results (getHomeTimeline))
  (def output (formatStatusesForOutput results))
  (doseq [tweet output] (println tweet))
  
  )
