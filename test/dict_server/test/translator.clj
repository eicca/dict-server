(ns dict-server.test.translator
  (:require [clojure.test :refer :all]
            [dict-server.translator :refer :all]))

(def translate-params
  {:from "en" :dest-locales ["de"] :phrase "hello"})

(defn assert-translations
  [expected]
  (let [res (translate translate-params)
        translations (-> res :meta-translations first :translations)]
    (is (= translations expected))))

(defn stub-translation-calls
  [google-stub glosbe-stub test]
  (with-redefs
    [dict-server.glosbe.translations/get-translations
     (fn [from dest phrase]
       glosbe-stub)
     dict-server.google-translate/translate
     (fn [from dest phrase]
       google-stub)]
    (test)))

(deftest test-translate
  (testing "when glosbe returns translations"
    (stub-translation-calls
     nil {:translations ["glosbe"]}
     (fn [] (assert-translations ["glosbe"]))))

  (testing "when glosbe returns empty result"
    (stub-translation-calls
     "google" {:translations []}
     (fn [] (assert-translations ["google"])))))

(deftest test-suggest
  (with-redefs
    [dict-server.glosbe.suggestions/get-all-suggestions
     (fn [params]
       {:suggestions ["foo"] :locale "en"})]
    (let [res (suggest {})]
      (is (= res [{:phrase "foo" :locale "en"}])))))
