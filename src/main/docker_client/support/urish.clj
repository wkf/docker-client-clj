(ns docker-client.support.urish
  (:import java.net.URI))

(defprotocol Coercible
  (coerce [this] "coerce input to urish map"))

(extend-protocol Coercible
  String
  (coerce [this]
    (coerce (java.net.URI. this)))

  clojure.lang.IPersistentMap
  (coerce [this] this)

  java.net.URL
  (coerce [this]
    (coerce (.toURI this)))

  java.net.URI
  (coerce [this]
    {:scheme (.getScheme this)
     :user (.getUserInfo this)
     :host (.getHost this)
     :port (.getPort this)
     :path (.getPath this)
     :query (.getQuery this)
     :fragment (.getFragment this)}))

(defn urish [x] (coerce x))

(defn ->uri [x]
  (let [u (urish x)]
    (java.net.URI. (:scheme x)
                   (:user x)
                   (:host x)
                   (:port x)
                   (:path x)
                   (:query x)
                   (:fragment x))))

(defn ->url [u]
  (.toURL (->uri u)))

(defn ->str [u]
  (.toString (->uri u)))

(def http? (comp #{"http"} :scheme urish))
(def https? (comp #{"https"} :scheme urish))

(comment
  (https? (-> (System/getenv "DOCKER_HOST") urish (assoc :scheme "https")))

  (https? (-> (System/getenv "DOCKER_HOST") urish (assoc :scheme "https") ->str)))
