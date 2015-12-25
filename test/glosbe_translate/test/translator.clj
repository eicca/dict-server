(ns glosbe-translate.test.translator
  (:require [clojure.test :refer :all]
            [glosbe-translate.translator :as t]))

(deftest test-translate
  (testing "hello from en to de"
    (let [res (t/translate "en" "de"  "hello")]
      (is (some? (res :meanings))))))

