(ns docker-client.support.rest-test
  (:refer-clojure :exclude [get])
  (:require [clojure.test :refer :all]
            [docker-client.support.rest :refer :all]))

(def test-wire-format
  {"Image" "hello"
   "HostConfig" {"Binds" ["a:b"]}
   "Env" ["TEST=hello"]})

(def test-api-format
  {:image "hello"
   :host-config {:binds ["a:b"]}
   :env ["TEST=hello"]})

(deftest format-body-and-params
  (testing "->wire-format uses string keys"
    (is (= (@#'docker-client.support.rest/->wire-format test-api-format) test-wire-format)))
  (testing "->api-format uses keyword keys"
    (is (= (@#'docker-client.support.rest/->api-format test-wire-format) test-api-format))))
