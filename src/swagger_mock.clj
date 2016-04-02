(ns swagger-mock
  (:require [io.sarnowski.swagger1st.core :as s1st]
            [io.sarnowski.swagger1st.util.api :as s1stapi]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]
            [ring.util.response :refer :all]
            [clojure.tools.logging :as log])
  (:gen-class))

(defn extract-example
  "Extracts the example from a response schema as data structure."
  [schema]
  (let [schema-type (get schema "type" "object")]
  (cond
    (= schema-type "object") (into {} (map (fn [[property property-schema]]
                                             [property (extract-example property-schema)])
                                           (get schema "properties")))
    (= schema-type "array") [(extract-example (get schema "items"))]
    :else (get schema "example"))))

(defn mock-request
  "Will be called for every defined HTTP endpoint. Should return the defined example."
  [request]
  (let [resp (get-in request [:swagger :request "responses"])
        ; take the lowest non-default response
        [stat resp] (first (sort-by key (remove (fn [[status _]] (= status "default")) resp)))
        resp (if (and resp (get resp "schema"))
               (-> (get resp "schema")
                   (extract-example)
                   (response)
                   (content-type "application/json")
                   (status (Integer. stat)))
               (-> (response nil)
                   (status (Integer. stat))))]
    (log/infof "%s %s -> %s" (-> request :request-method name .toUpperCase) (:uri request) (:status resp))
    resp))

(defn setup-mock-handlers
  "Sets the mock operationId into all operations."
  [context]
  (assoc-in context [:definition "paths"]
            (into {} (map (fn [[path path-definition]]
                            [path (into {} (map (fn [[operation operation-definition]]
                                                  [operation (assoc operation-definition "operationId" "swagger-mock/mock-request")])
                                                path-definition))])
                          (get-in context [:definition "paths"])))))

(defn setup-handler
  "Creates a ring handler chain with swagger1st."
  [file]
  (-> (s1st/context :yaml-file file)
      (setup-mock-handlers)
      (s1st/ring s1stapi/add-cors-headers)
      (s1st/ring s1stapi/surpress-favicon-requests)
      (s1st/discoverer)
      (s1st/mapper)
      (s1st/parser)
      (s1st/executor)))

(defn -main
  "Usage:  java -jar swagger-mock.jar myapp.yaml"
  [definition-file & args]
  (let [handler (setup-handler definition-file)
        config (merge {:port 8181} env)]
    (jetty/run-jetty handler config)))
