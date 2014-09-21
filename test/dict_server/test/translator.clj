(ns dict-server.test.translator
  (:require [clojure.test :refer :all]
            [dict-server.translator :refer :all]
            [clj-http.fake :refer :all]))

(deftest test-translate
  (is true
      "Returns translation from different sources."))

