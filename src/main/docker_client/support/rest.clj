(ns docker-client.support.rest
  (:refer-clojure :exclude [get])
  (:require [bidi.bidi :as bidi]
            [clj-http.client :as http]
            [camel-snake-kebab.core :refer [->PascalCaseString ->kebab-case-keyword]]
            [camel-snake-kebab.extras :refer [transform-keys]]
            [docker-client.support.urish :refer [urish] :as urish]))

(defn- ->api-format [m]
  (transform-keys ->kebab-case-keyword m))

(defn- ->wire-format [m]
  (transform-keys ->PascalCaseString m))

(defn path-for
  [routes route-name route-params]
  (apply bidi/path-for routes route-name (flatten (seq route-params))))

(defn request
  ([f client route-name route-params]
   (request f client route-name route-params {}))
  ([f client route-name route-params options]
   (let [path (path-for (:routes client) route-name route-params)]
     (-> (f
          (-> client :uri (assoc :path path) urish/->str)
          (-> client :options (merge options)))
         :body
         ->api-format))))

(def get (partial request http/get))
(def delete (partial request http/delete))

(defn post
  ([client params route-name route-params]
   (post client params route-name route-params {}))
  ([client params route-name route-params options]
   (request http/post client route-name route-params
            (merge {:form-params (->wire-format params)} options))))

(def put (partial request http/put))
