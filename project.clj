(defproject swagger-mock "latest"
  :description "Runs an HTTP server based on a swagger definition and returns mocked responses."
  :url "https://github.com/zalando/swagger-mock"

  :license {:name "The Apache License, Version 2.0"
            :url  "http://www.apache.org/licenses/LICENSE-2.0"}

  :min-lein-version "2.0.0"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [io.sarnowski/swagger1st "0.21.0"]
                 [ring "1.4.0"]
                 [environ "1.0.0"]
                 [org.apache.logging.log4j/log4j-api "2.3"]
                 [org.apache.logging.log4j/log4j-core "2.3"]
                 [org.apache.logging.log4j/log4j-slf4j-impl "2.3"]
                 [org.apache.logging.log4j/log4j-jcl "2.3"]
                 [org.apache.logging.log4j/log4j-1.2-api "2.3"]
                 [org.apache.logging.log4j/log4j-jul "2.3"]]

  :main ^:skip-aot swagger-mock
  :uberjar-name "swagger-mock.jar"
  :profiles {:uberjar {:aot :all}}

  :plugins [[io.sarnowski/lein-docker "1.1.0"]]

  :docker {:image-name "zalando/swagger-mock"}

  :release-tasks [["vcs" "assert-committed"]
                  ["clean"]
                  ["uberjar"]
                  ["docker" "build"]
                  ["docker" "push"]])
