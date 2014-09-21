(ns dict-server.test.translator
  (:require [clojure.test :refer :all]
            [dict-server.utils :refer :all]))

(deftest test-locale-to-alpha3
  (is (= (locale-to-alpha3 "en")
         "eng")
      "Converts 'en' to 'eng'")
  (is (= (locale-to-alpha3 "de")
         "deu")
      "Converts 'de' to 'deu'"))

