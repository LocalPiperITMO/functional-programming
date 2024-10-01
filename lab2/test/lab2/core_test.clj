(ns lab2.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [lab2.core :as trie]))

;; Unit Tests
(deftest test-add-word
  (testing "Add word and check if it's present"
    (let [test-trie (trie/insert-word (trie/trie-node) "hello")
          test-number-trie (trie/insert-word (trie/trie-node) 12312)]
      (is (= true (trie/search-word test-trie "hello")))
      (is (= true (trie/search-word test-number-trie 12312))))
    ))

(deftest test-add-multiple-words
  (testing "Add multiple words and check if they are present"
    (let [test-trie (trie/insert-word (trie/insert-word (trie/trie-node) "hello") "world")
          test-number-trie (trie/insert-word (trie/insert-word (trie/trie-node) 1234) 5678)]
      (is (every? #(trie/search-word test-trie %) ["hello" "world"]) )
      (is (every? #(trie/search-word test-number-trie %) [1234 5678])))))

(deftest test-remove-word
  (testing "Add and Remove word"
    (let [test-trie (trie/remove-word (trie/insert-word (trie/trie-node) "hello") "hello")
          test-number-trie (trie/remove-word (trie/insert-word (trie/trie-node) 1234) 1234)]
      (is (= false (trie/search-word test-trie "hello")))
      (is (= false (trie/search-word test-number-trie 1234))))))

(deftest test-map-trie
  (testing "Map trie to collection and check if all words are intact"
    (let [test-trie (trie/insert-word (trie/insert-word (trie/trie-node) "hello") "world")
          trie-map (trie/map-trie test-trie)
          test-number-trie (trie/insert-word (trie/insert-word (trie/trie-node) 1234) 5678)
          number-map (trie/map-trie test-number-trie)]
      (is (= (set ["hello" "world"]) (set trie-map)))
      (is (= (set [1234 5678]) (set number-map))))))

(deftest test-map-unmap
  (testing "Map trie to collection, then build a new trie from collection, check 2 tries for equalty"
    (let [trie1 (trie/insert-word (trie/insert-word (trie/trie-node) "hello") "world")
          trie-map (trie/map-trie trie1)
          trie2 (trie/trie-collection trie-map)
          trie3 (trie/insert-word (trie/insert-word (trie/trie-node) 1234) 5678)
          number-map (trie/map-trie trie3)
          trie4 (trie/trie-collection number-map)]
      (is (= true (trie/compare-trie trie1 trie2)))
      (is (= true (trie/compare-trie trie3 trie4))))))

(deftest test-filter-trie
  (testing "Filter trie, then check if trie is filtered"
    (let [word-list ["hello" "hola" "hi" "aboba" "amogus" "bubus"]
          number-list [11 123 678 124 16 17 79]
          pr1 #(= \a %)
          pr2 #(pr1 %)
          pr3 #(even? %)
          trie1 (trie/trie-collection word-list)
          trie2 (trie/filter-trie trie1 pr2)
          trie3 (trie/trie-collection number-list)
          trie4 (trie/filter-trie trie3 pr3)
          passed-words (filter pr2 word-list)
          passed-nums (filter pr3 number-list)
          ]
      (is (= (set passed-words) (set (trie/map-trie trie2))))
      (is (= (set passed-nums) (set (trie/map-trie trie4)))))))

(deftest test-fold-trie
  (testing "Folding tries using different functions"
    (let [test-trie (trie/trie-collection ["a" "apple" "application" "banana" "cherry"])
          predicate1 #(str %1 "-" %2)]
      (is (= (trie/fold-trie-left predicate1 test-trie) "a-apple-application-banana-cherry"))
      (is (= (trie/fold-trie-right predicate1 test-trie) "cherry-banana-application-apple-a"))))) ;; TODO: add left-side-view and right-side-view folds

;; Logic Tests
(deftest logic-merge
  (testing "Check trie union"
    (let [A (trie/trie-collection ["a" "apple" "application" "banana" "cherry"])
          B (trie/trie-collection ["b" "banana"])
          C (trie/trie-collection ["a" "apple" "application" "b" "banana" "cherry"])]
      (is (= true (trie/compare-trie C (trie/merge-trie A B))))))
  )

(deftest logic-intersect
  (testing "Check trie intersection"
    (let [A (trie/trie-collection ["a" "apple" "application" "banana" "cherry"])
          B (trie/trie-collection ["b" "banana"])
          C (trie/trie-collection ["banana"])]
      (is (= true (trie/compare-trie C (trie/intersect-trie A B)))))))

(deftest logic-subtract
  (testing "Check trie subtraction"
    (let [A (trie/trie-collection ["a" "apple" "application" "banana" "cherry"])
          B (trie/trie-collection ["b" "banana"])
          C (trie/trie-collection ["a" "apple" "application" "cherry"])]
      (is (= true (trie/compare-trie C (trie/subtract-trie A B)))))))

(deftest logic-xor
  (testing "Check trie XOR"
    (let [A (trie/trie-collection ["a" "apple" "application" "banana" "cherry"])
          B (trie/trie-collection ["b" "banana"])
          C (trie/trie-collection ["a" "apple" "application" "b" "cherry"])]
      (is (= true (trie/compare-trie C (trie/xor-trie A B)))))))

;; Property Tests
(deftest property-associative
  (testing "Check property A ⋅ (B ⋅ C) = (A ⋅ B) ⋅ C"
    (let [A (trie/trie-collection ["some" "words" "for" "trie"])
          B (trie/trie-collection ["other" "words"])
          C (trie/trie-collection ["this" "should" "pass"])]
      (is (= true (trie/compare-trie (trie/merge-trie A (trie/merge-trie B C)) (trie/merge-trie (trie/merge-trie A B) C))))
      (is (= true (trie/compare-trie (trie/intersect-trie A (trie/intersect-trie B C)) (trie/intersect-trie (trie/intersect-trie A B) C))))
      (is (= true (trie/compare-trie (trie/xor-trie A (trie/xor-trie B C)) (trie/xor-trie (trie/xor-trie A B) C))))
      )))

(deftest property-commutative
  (testing "Check property A ⋅ B = B ⋅ A"
    (let [A (trie/trie-collection ["a" "b"])
          B (trie/trie-collection ["c" "d"])]
      (is (= true (trie/compare-trie (trie/merge-trie A B) (trie/merge-trie B A))))
      (is (= true (trie/compare-trie (trie/intersect-trie A B) (trie/intersect-trie B A))))
      (is (= true (trie/compare-trie (trie/xor-trie A B) (trie/xor-trie B A))))
      )))

(deftest property-distributive
  (testing "Check property A & (B | C) = (A & B) | (A & C)")
  (let [A (trie/trie-collection ["some" "words" "for" "trie"])
        B (trie/trie-collection ["other" "words"])
        C (trie/trie-collection ["this" "should" "pass"])]
    (is (= true (trie/compare-trie (trie/intersect-trie A (trie/merge-trie B C)) (trie/merge-trie (trie/intersect-trie A B) (trie/intersect-trie A C)))))))

(deftest property-neutral
  (testing "Check property A | O = A, A & O = O"
    (let [A (trie/trie-collection ["a"])
          O (trie/trie-node)]
      (is (= true (trie/compare-trie (trie/merge-trie A O) A)))
      (is (= true (trie/compare-trie (trie/intersect-trie A O) O)))
      )))

(deftest property-xor
  (testing "Check properties of XOR: A ⊕ A = O, A ⊕ A ⊕ A = A"
    (let [A (trie/trie-collection ["AAAAAAAAAAAAAAAAA"])
          O (trie/trie-node)]
      (is (= true (trie/compare-trie (trie/xor-trie A A) O)))
      (is (= true (trie/compare-trie (trie/xor-trie (trie/xor-trie A A) A) A))))
    ))