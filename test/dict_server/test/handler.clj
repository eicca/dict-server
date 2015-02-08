(ns dict-server.test.handler
  (:require [clojure.test :refer :all]
            [dict-server.handler :refer :all]
            [ring.mock.request :as mock]))

(deftest test-handler
  (testing "translations with 1 dest locale"
    (with-redefs [dict-server.translator/translate
                  (fn [params] (params :dest-locales))]
      (let [response (app (mock/request :get "/translations"
                                        {:dest-locales "en"}))]
        (is (= (:status response) 200))
        (is (= (:body response) "[\"en\"]")))))

  (testing "translations with 2 dest locales"
    (with-redefs [dict-server.translator/translate
                  (fn [params] (params :dest-locales))]
      (let [response (app (mock/request :get "/translations"
                                        {:dest-locales ["en" "de"]}))]
        (is (= (:status response) 200))
        (is (= (:body response) "[\"en\",\"de\"]")))))

  (testing "suggestions"
    (with-redefs [dict-server.translator/suggest
                  (fn [params] (params :foo))]
      (let [response (app (mock/request :get "/suggestions" {:foo "bar"}))]
        (is (= (:status response) 200))
        (is (= (:body response) "bar")))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))
