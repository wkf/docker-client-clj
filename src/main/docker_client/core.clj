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

(def routes
  ["/"
   {"auth" :auth
    "info" :info
    "_ping" :ping
    "version" :version
    "commit" :create-image-from-container
    "events" :events
    "build" :build-image
    "images" {"/get" :dump-images
              "/load" :load-images
              "/json" :images
              "/create" :create-image
              "/search" :search-images
              ["/" :image-name] {"" :remove-image
                                 "/json" :image
                                 "/history" :image-history
                                 "/get" :dump-image
                                 "/tag" :tag-image
                                 "/push" :push-image}}
    "containers" {"/json" :containers
                  "/create" :create-container
                  ["/" :container-id] {"" :remove-container
                                       "/top" :container-processes
                                       "/logs" :container-logs
                                       "/stats" :container-stats
                                       "/changes" :container-changes
                                       "/json" :container
                                       "/stop" :stop-container
                                       "/kill" :kill-container
                                       "/start" :start-container
                                       "/export" :export-container
                                       "/resize" :resize-container-tty
                                       "/restart" :restart-container
                                       "/rename" :rename-container
                                       "/pause" :pause-container
                                       "/unpause" :unpause-container
                                       "/attach" :attach-to-container
                                       "/attach/ws" :attach-to-container-ws
                                       "/exec" :create-exec-in-container
                                       "/wait" :wait-for-container
                                       "/copy" :copy-from-container}}
    "exec" {["/" :exec-id] {"/json" :exec
                            "/start" :start-exec
                            "/resize" :resize-exec-tty}}}])

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
      :routes routes
      :options (-> (when (urish/https? uri)
                     (security/options
                       (or (:cert-path config)
                           (:docker-cert-path env))))
                   (merge {:as :json-string-keys
                           :accept :json
                           :content-type :json})
                   (merge (:options config)))})))

(defn inspect-container
  ([c id]
   (inspect-container c id {}))
  ([c id options]
   (rest/get c :container {:container-id id} options)))

(defn start-container!
  ([c id]
   (start-container! c id {}))
  ([c id options]
   (rest/post c {} :start-container {:container-id id} options)))

(defn stop-container!
  ([c id]
   (stop-container! c id {}))
  ([c id options]
   (rest/post c {} :stop-container {:container-id id} options)))

(defn remove-container!
  ([c id]
   (remove-container! c id {}))
  ([c id options]
   (rest/delete c :remove-container {:container-id id} options)))

(defn create-container!
  ([c spec]
   (create-container! c spec {}))
  ([c spec options]
   (let [[spec'
          options'] (if-let [n (:name spec)]
                      [(dissoc spec :name)
                       (update-in options [:query-params] merge {"name" n})]
                      [spec
                       options])]
     (rest/post c spec' :create-container {} options'))))
