(ns dict-server.test.utils
  (:require [clojure.test :refer :all]
            [dict-server.utils :refer :all]))

(deftest test-pairs-for
  (let [pairs (pairs-for ["en" "de" "ru"] "en")]
    (is (= pairs [{:from "en", :dest "de"} {:from "en", :dest "ru"}]))))
