(ns streaming-api.core
  (:gen-class)
  (:import [twitter4j
              StatusListener
              TwitterStream TwitterStreamFactory
              FilterQuery]
           [twitter4j.conf ConfigurationBuilder]))

;(defn foo []
;(proxy [StatusListener] []
;  (onStatus [status] (println status))
;)
;)
(set! *warn-on-reflection* true)

(defn -main []


(defn config []
  (let [cb (ConfigurationBuilder.)]
    (.setUser cb "USERNAME")
    (.setPassword cb "PASSWORD")
    (.build cb)))


;(def filter-query (FilterQuery. 0, (long-array []), (into-array String ["clojure"])))
(def filter-query (FilterQuery. 0, (long-array []), (into-array String ["twitter"])))


(defn status-listener []
  (proxy [StatusListener] []
    (onStatus [^twitter4j.Status status] (println (.getText status)))
    (onException [^java.lang.Exception e] (.printStackTrace e))
    (onDeletionNotice [^twitter4j.StatusDeletionNotice statusDeletionNotice] ())
    (onScrubGeo [userId upToStatusId] ())
    (onTrackLimitationNotice [numberOfLimitedStatuses] ())))


;(def aaa (.getFilterStream (TwitterStreamFactory. (config)) filter-query))

(def bbb (.getInstance (TwitterStreamFactory. (config))))
(def b (.getFilterStream bbb filter-query))





(while true
  (.next b (status-listener)))





;
;
;(def bbb (.getInstance (TwitterStreamFactory. (config))))
;(.addListener bbb (status-listener))
;(.sample bbb)

  
)



;(.addListener bbb (proxy [StatusListener] []
;  (onStatus [^twitter4j.Status status] (println (.getText status)))
;  (onException [^java.lang.Exception e] (.printStackTrace e))
;  (onDeletionNotice [^twitter4j.StatusDeletionNotice statusDeletionNotice] ())
;  (onScrubGeo [userId upToStatusId] ())
;  (onTrackLimitationNotice [numberOfLimitedStatuses] ())))


;ConfigurationBuilder cb = new ConfigurationBuilder();
;cb.setDebugEnabled(true)
;  .setOAuthConsumerKey("*********************")
;  .setOAuthConsumerSecret("******************************************")
;  .setOAuthAccessToken("**************************************************")
;  .setOAuthAccessTokenSecret("******************************************");
;TwitterFactory tf = new TwitterFactory(cb.build());
;Twitter twitter = tf.getInstance();


;(proxy [baseclass-name] [baseclass-constructor-parameters]
;    method-redefinition-1
;    ...
;    method-redefinition-N)
;where a method redefinition looks like:
;
;(method-name [parameters]
;    method-body)


;public static void main(String[] args) throws TwitterException, IOException{
;    StatusListener listener = new StatusListener(){
;        public void onStatus(Status status) {
;            System.out.println(status.getUser().getName() + " : " + status.getText());
;        }
;        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
;        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
;        public void onException(Exception ex) {
;            ex.printStackTrace();
;        }
;    };
;    TwitterStream twitterStream = new TwitterStreamFactory(listener).getInstance();
;    // sample() method internally creates a thread which manipulates TwitterStream and calls these adequate listener methods ;continuously.
;    twitterStream.sample();
;}
