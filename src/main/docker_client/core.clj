(ns docker-client.core
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [docker-client.support.rest :as rest]
            [docker-client.support.security :as security]
            [docker-client.support.urish :refer [urish] :as urish]))

(def env
  {:docker-host (System/getenv "DOCKER_HOST")
   :docker-cert-path (System/getenv "DOCKER_CERT_PATH")
   :docker-tls-verify (System/getenv "DOCKER_TLS_VERIFY")})

(defn- parse-uri-from-env []
  (-> env :docker-host urish
      (assoc
        :scheme (if (= "1" (:docker-tls-verify env)) "https" "http"))
      urish/->str))

(defn client
  ([]
   (client {}))
  ([config]
   (let [uri (urish (or (:uri config) (parse-uri-from-env)))]
     {:uri uri
      :routes (-> (or (:routes config) "routes.edn") io/resource slurp edn/read-string)
      :options (-> (when (urish/https? uri)
                     (security/options
                       (or (:cert-path config)
                           (:docker-cert-path env))))
                   (merge {:as :json-string-keys
                           :accept :json
                           :content-type :json})
                   (merge (:options config)))})))

(defn inspect-container [c id]
  (rest/get c :container {:container-id id}))

(defn start-container! [c id]
  (rest/post c {} :start-container {:container-id id}))

(defn stop-container! [c id]
  (rest/post c {} :stop-container {:container-id id}))

(defn remove-container! [c id]
  (rest/delete c :remove-container {:container-id id}))

(defn create-container!
  ([c spec]
   (rest/post c spec :create-container {}))
  ([c nm spec]
   (rest/post c spec :create-container {} {:query-params {"name" nm}})))
