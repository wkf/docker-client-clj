# docker-client

[![Build Status](https://secure.travis-ci.org/wkf/docker-client-clj.svg)](http://travis-ci.org/wkf/docker-client-clj)

An incomplete Docker remote API client written in Clojure. Supports TLS and API version `1.18`.

## Installation

To install, add the following dependency to your project.clj file:

[![Clojars Project](http://clojars.org/docker-client/latest-version.svg)](http://clojars.org/docker-client)

## Usage

To get started, require the core namespace:

```clojure
(require '[docker-client.core :as docker])
```

Next, create a docker client. By default, configuration is pulled from the `DOCKER_HOST`, `DOCKER_CERT_PATH` and `DOCKER_VERIFY_TLS` environment variables. If DOCKER_VERIFY_TLS and DOCKER_CERT_PATH are both set, read credentials from disk and use https. This behavior can be overriden by passing configuration to the `docker/client` function:

```clojure
;; creates a client with default configuration from the environment
(def c
  (docker/client))

;; configuration can also be overriden
(def c
  (docker/client {:uri "http://localhost:4343"}))

```

Once you have a client, you pass it to any of the implemented public functions. So far, that includes:

```clojure
;; create a container
(let [{id :id} (create-container!
                 c
                 {:cmd ["sleep" "100"]
                  :env ["A_VAR=a_value"]
                  :image "debian:wheezy"})]

  ;; inspect its configuration
  (inspect-container c id)

  ;; start the container
  (start-container! c id)

  ;; stop the container
  (stop-container! c id)

  ;; remove the container
  (remove-container! c id))
```

## Tests
This project uses core.test, so to run tests:

`lein test`

## Future

This client is a work in progress; there are many endpoints left to implement. If you have a pressing need for a particular feature, create an issue.

## License

Copyright © 2015 [Will Farrell](http://willfarrell.is)

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
