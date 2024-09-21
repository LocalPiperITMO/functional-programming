(ns lab1.core-test
  (:require [clojure.test :refer :all]
            [lab1.core :refer :all]))

(deftest a-test
  (testing "TEST1"
    (is (= 1 1))
    (is (= (+ 1 2) 3))
    (is (= (- 1 1) 0)))
  (testing "TEST2"
    (is (= 2 3))))
