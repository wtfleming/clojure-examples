(ns streaming-api.core
  (:gen-class)
  (:import [twitter4j
              StatusListener TwitterStream TwitterStreamFactory
              FilterQuery]
           [twitter4j.conf ConfigurationBuilder Configuration]
           [twitter4j.json DataObjectFactory]))

;(set! *warn-on-reflection* true)


(defn config-with-password ^Configuration []
  "Build a twitter4j configuration object with a username/password pair"
  (let [cb (ConfigurationBuilder.)]
    (.setUser cb "YOUR-USERNAME")
    (.setPassword cb "YOUR-PASSWORD")

     ; Uncomment if you want access to the raw json
     ;(.setJSONStoreEnabled cb true)

    (.build cb)))



(defn status-listener []
  "Implementation of twitter4j's StatusListener interface"
  (proxy [StatusListener] []

    (onStatus [^twitter4j.Status status]
      (println (.getText status))
      ; How to get raw json - make sure it's uncommented in the config
      ; or else will print nil.
      ;(println (DataObjectFactory/getRawJSON status)
    )

    (onException [^java.lang.Exception e] (.printStackTrace e))
    (onDeletionNotice [^twitter4j.StatusDeletionNotice statusDeletionNotice] ())
    (onScrubGeo [userId upToStatusId] ())
    (onTrackLimitationNotice [numberOfLimitedStatuses] ())))


(defn get-twitter-stream-factory[]
  (let [factory (TwitterStreamFactory. (config-with-password))]
    (.getInstance factory)))


(defn do-filter-stream []
  ; We want tweets with the word tweet in them
  (let [filter-query (FilterQuery. 0 (long-array []) (into-array String ["tweet"]))
        stream (.getFilterStream (get-twitter-stream-factory) filter-query)]
    (while true
      (.next stream (status-listener)))))


(defn do-sample-stream []
  (let [stream (get-twitter-stream-factory)]
    (.addListener stream (status-listener))
    (.sample stream)))


(defn -main []
  (do-filter-stream)
;  (do-sample-stream)
 
)



; In event want to use OAuth, here's the java
;ConfigurationBuilder cb = new ConfigurationBuilder();
;  cb.setDebugEnabled(true)
;  .setOAuthConsumerKey("*********************")
;  .setOAuthConsumerSecret("******************************************")
;  .setOAuthAccessToken("**************************************************")
;  .setOAuthAccessTokenSecret("******************************************");
;TwitterFactory tf = new TwitterFactory(cb.build());



