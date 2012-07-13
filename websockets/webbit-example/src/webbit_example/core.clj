(ns webbit-example.core
  (:require [cheshire.core :as json]
            [clojure.string :as s])
  (:import [org.webbitserver WebServer WebServers WebSocketHandler]
           [org.webbitserver.handler StaticFileHandler]))


(defn on-message [connection json-message]
  (let [m (json/parse-string json-message true)
        {{message :message} :data} m]
    (.send connection (json/generate-string
                       {:type "upcased" :message (s/upper-case message)}))))

(defn -main []
  (doto (WebServers/createWebServer 8080)
    (.add "/websocket"
          (proxy [WebSocketHandler] []
            (onOpen [c] (prn "opened" c))
            (onClose [c] (prn "closed" c))
            (onMessage [c j] (on-message c j))))
    (.add (StaticFileHandler. "."))
    (.start)))

