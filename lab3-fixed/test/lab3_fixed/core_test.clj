(ns lab3-fixed.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [lab3-fixed.linear :refer [linear-interpolation]]
            [lab3-fixed.lagrange :refer [lagrange-polynomial lagrange-interpolation]]))

(deftest test-linear-interpolation
  (testing "Linear interpolation between two points"
    (let [points [[0.0 0.0] [1.0 1.0]]
          step 0.5
          expected [[0.0 0.0] [0.5 0.5] [1.0 1.0]]]
      (is (= expected (vec (linear-interpolation points step)))))))

(deftest test-lagrange-polynomial
  (testing "Lagrange internal logic"
    (let [points [[1 1] [2 4] [3 9]]]
      (is (= (lagrange-polynomial points 2) 4N))
      (is (= (lagrange-polynomial points 3) 9N))
      (is (= (lagrange-polynomial points 1) 1N))
      (is (= (lagrange-polynomial [[1 2]] 1) 2N)))
    )
  )

(deftest test-lagrange-interpolation
  (testing "Lagrange interpolation between three points"
       (let [points [[1 1] [2 4] [3 9]]
             step 0.5
             start-x 1
             end-x 3
             expected [[1.0 1.0]
                       [1.5 2.25]
                       [2.0 4.0]
                       [2.5 6.25]
                       [3.0 9.0]]]
         #_{:clj-kondo/ignore [:redundant-let]}
         (let [result (lagrange-interpolation points step start-x end-x)]
           (is (= (map #(map double %) result) expected))))  
    )
  )