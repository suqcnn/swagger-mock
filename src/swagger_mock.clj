(ns swagger-mock
  (:require [io.sarnowski.swagger1st.core :as s1st]
            [ring.middleware.params :refer [wrap-params]]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]
            [clojure.walk :as walk]
            [ring.util.response :refer :all]
            [clojure.tools.logging :as log]
            [clojure.data.json :as json])
  (:gen-class))

(defn extract-example
  "Extracts the example from a response definition as data structure."
  [stat response-definition request]
  (let [resp (get-in response-definition ["schema" "example"])]
    (if (empty? resp)
      (if (= 200 stat)
        (log/warnf "No example found for %s %s" (-> request :request-method name .toUpperCase) (:uri request)))
      (if (string? resp)
        (do
          (json/read-str resp))
        resp))))

(defn mock-request
  "Will be called for every defined HTTP endpoint. Should return the defined example."
  [request]
  (let [resp (get-in request [:swagger-request "responses"])
        ; take the lowest non-default response
        [stat resp] (first (sort-by key (remove (fn [[status _]] (= status "default")) resp)))
        ; get its example
        resp (extract-example stat resp request)
        ; form response
        resp (if resp
               (-> resp
                   (response)
                   (content-type "application/json")
                   (status stat))
               (-> (response nil)
                   (status stat)))]
    (log/infof "%s %s -> %s" (-> request :request-method name .toUpperCase) (:uri request) (:status resp))
    resp))

(defn setup-mock-handlers
  "Overrides all operation IDs to use our mock function."
  [definition]
  (walk/postwalk (fn [element]
                   (if (and (map? element)
                            (contains? element "operationId"))
                     (assoc element "operationId" "swagger-mock/mock-request")
                     element))
                 definition))

(defn setup-handler
  "Creates a ring handler chain with swagger1st."
  [file]
  (let [definition (s1st/load-swagger-definition ::s1st/yaml-file file)
        definition (setup-mock-handlers definition)]
    (-> (s1st/swagger-executor)
        (s1st/swagger-validator)
        (s1st/swagger-parser)
        (s1st/swagger-discovery)
        (s1st/swagger-mapper ::s1st/direct definition
                             :cors-origin "*")
        (wrap-params))))

(defn -main
  "Usage:  java -jar swagger-mock.jar myapp.yaml"
  [definition-file & args]
  (let [handler (setup-handler definition-file)
        config (merge {:port 8181} env)]
    (jetty/run-jetty handler config)))
