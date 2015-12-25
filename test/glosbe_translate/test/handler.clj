(ns glosbe-translate.test.handler
  (:require [clojure.test :refer :all]
            [glosbe-translate.handler :refer [app]]
            [cheshire.core :refer [generate-string
                                   parse-string]]
            [ring.mock.request :as mock]))

(defn json-body [request data]
  (-> request
      (mock/content-type "application/json")
      (mock/body (generate-string data))))

(defn mock-request
  [verb endpoint body]
  (-> (app (-> (mock/request verb endpoint)
               (json-body body)))
      :body
      (parse-string true)))

(deftest test-handler
  (testing "accepts post"
    (-> (mock-request :post "/translations"
                      {:source "en" :target "de" :query "hello"})
        :meanings
        (is some?)))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))

;; (run-tests)

