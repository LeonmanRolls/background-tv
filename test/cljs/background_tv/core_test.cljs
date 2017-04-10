(ns background-tv.core-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [background-tv.core :as core]))

(deftest fake-test
  (testing "fake description"
    (is (= 1 2))))
