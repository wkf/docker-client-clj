(ns docker-client.support.security-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [docker-client.support.security :refer :all]))

(deftest options-contains-keystore-and-trust-store
  (testing "options creates keystore and trust-store from cert path"
    (let [opts (-> "certs" io/resource io/file str options)]
      (is (-> opts :keystore type (= java.security.KeyStore)))
      (is (-> opts :keystore-pass type (= String)))
      (is (-> opts :trust-store type (= java.security.KeyStore))))))
