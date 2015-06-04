(defproject docker-client "0.1.1-SNAPSHOT"
  :description "A Docker client written in Clojure. Supports TLS/HTTPS."
  :url "https://github.com/wkf/docker-client-clj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :test-paths ["src/test"]
  :source-paths ["src/main"]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/data.json "0.2.6"]
                 [org.bouncycastle/bcpkix-jdk15on "1.52"]
                 [clj-http "1.1.2"]
                 [bidi "1.18.9" :exclusions [org.clojure/clojure]]
                 [camel-snake-kebab "0.3.1" :exclusions [org.clojure/clojure]]]
  :plugins [[lein-cloverage "1.0.6"]]
  :deploy-repositories [["releases" :clojars]]
  :profiles {:dev {:source-paths ["src/dev"]
                   :dependencies [[clj-http-fake "1.0.1"]]}})
