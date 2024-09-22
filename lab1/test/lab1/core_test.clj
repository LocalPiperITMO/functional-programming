(ns lab1.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [lab1.core :refer :all]))

(def ans8 23514624000)
(def ans23 4179871)

(deftest problem8-tests
  (testing "Project Euler Problem 8: Largest Product in a Series"
    (is (= ans8 (largest-product-in-series-basic-recursion 0 0)))
    (is (= ans8 (largest-product-in-series-tail-recursion)))
    (is (= ans8 (largest-product-in-series-modular)))
    (is (= ans8 (largest-product-in-series-map)))
    (is (= ans8 (largest-product-in-series-loop)))
    (is (= ans8 (largest-product-in-series-lazy 0)))))

(deftest problem23-tests
  (testing "Project Euler Problem 23: Non-Abundant Sums"
    (is (= ans23 (non-abundant-sums-basic-recursion 0)))
    (is (= ans23 (non-abundant-sums-tail-recursion 0 0)))
    (is (= ans23 (non-abundant-sums-modular 0)))
    (is (= ans23 (non-abundant-sums-map 0)))
    (is (= ans23 (non-abundant-sums-loop 0)))
    (is (= ans23 (non-abundant-sums-lazy 0)))))


