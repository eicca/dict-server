(ns dict-server.test.glosbe-client
  (:require [clojure.test :refer :all]
            [dict-server.glosbe-client :refer :all]
            [clj-http.fake :refer :all]))

(def dummy-obj
  {:phrase {:text "phrase-text" :language "deu"}
   :someCrap []
   :meanings [{:text "foo-eng" :language "eng"}
              {:text "foo-deu" :language "deu"}
              {:text "foo-eng2" :language "eng"}]})

(deftest test-first-meaning
  (is (= (first-meaning "en" (dummy-obj :meanings))
         "foo-eng")
      "Returns only first meaning obj for lang."))

(deftest test-compose-translation
  (is (= (compose-translation "en" "de" dummy-obj)
         {:phrase "phrase-text" :meaning-from "foo-eng" :meaning-dest "foo-deu"})
      "Makes structure of translation flat."))

(deftest test-rebuild-results
  (is (= (rebuild-results {:from "en" :dest "de"} [dummy-obj])
         [{:phrase "phrase-text" :meaning-from "foo-eng" :meaning-dest "foo-deu"}])
      "Rebuilds array of translations by applying compose-translation."))

(deftest test-prepare-params
  (is (= (prepare-params {:from "en" :dest "de" :phrase "brother"})
         {:from "eng", :dest "deu", :phrase "brother"})
      "Transforms params locales to alpha3."))

(def api-endpoint-matcher
  (re-pattern (str api-endpoint ".*")))

(def dummy-resp-body
  (slurp "test/dict_server/test/dummy_resp.json"))

(deftest test-get-translations
  (with-fake-routes
    {api-endpoint-matcher (fn [req] {:status 200 :headers [] :body dummy-resp-body})}
    (is (= (get-translations {:from "en" :dest "de" :phrase "foo"})
           {:dest "de", :from "en", :phrase "foo", :translations
            [{:phrase "bar" :meaning-from "bar-from"
              :meaning-dest "bar-dest-1"}
             {:phrase "baz" :meaning-from "baz-from"
              :meaning-dest "baz-dest"}]})
        "Gets translation info for a phrase.")))

