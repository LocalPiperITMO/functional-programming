(ns lab2.core-test
  (:require [clojure.set :refer [difference intersection union]]
            [clojure.test :refer [deftest is testing]]
            [clojure.test.check.generators :as gen]
            [lab2.core :as trie]))

;; Constants
(def numwords 30)
(def lower-case (map char (range (int \a) (inc (int \z)))))
(def upper-case (map char (range (int \A) (inc (int \Z)))))
(def all-letters (concat lower-case upper-case))

(defn random-string [length]
  (apply str (repeatedly length #(rand-nth all-letters))))

(defn generate-words [n]
  (repeatedly (+ (rand-int n) 1) #(random-string (+ (rand-int n) 1))))

(defn generate-one-word []
  (random-string (inc (rand-int numwords))))

(defn generate-one-small-number []
  (first (gen/sample (gen/large-integer* {:min 0, :max 1000}) 1)))

(defn generate-one-large-number []
  (first (gen/sample (gen/large-integer* {:min 0, :max 1000000}) 1)))

(defn generate-small-numbers [n]
  (gen/sample (gen/large-integer* {:min 0, :max 1000}) (+ (rand-int n) 1)))

(defn generate-numbers [n]
  (repeatedly (+ (rand-int n) 1) #(generate-small-numbers (+ (rand-int n)))))

;; Unit Tests
(deftest test-add-word
  (testing "Add word and check if it's present"
    (let [word (generate-one-word)
          number [(generate-one-small-number)]
          test-trie (trie/insert-word (trie/trie-node) word)
          test-number-trie (trie/insert-word (trie/trie-node) number)]
      (is (= true (trie/search-word test-trie word)))
      (is (= true (trie/search-word test-number-trie number))))))

(deftest test-add-multiple-words
  (testing "Add multiple words and check if they are present"
    (let [words [(generate-one-word) (generate-one-word)]
          numbers [[(generate-one-small-number)] [(generate-one-large-number)]]
          test-trie (trie/insert-word (trie/insert-word (trie/trie-node) (first words)) (last words))
          test-number-trie (trie/insert-word (trie/insert-word (trie/trie-node) (first numbers)) (last numbers))]
      (is (every? #(trie/search-word test-trie %) words))
      (is (every? #(trie/search-word test-number-trie %) numbers)))))

(deftest test-remove-word
  (testing "Add and Remove word"
    (let [word (generate-one-word)
          number [(generate-one-large-number)]
          test-trie (trie/remove-word (trie/insert-word (trie/trie-node) word) word)
          test-number-trie (trie/remove-word (trie/insert-word (trie/trie-node) number) number)]
      (is (= false (trie/search-word test-trie word)))
      (is (= false (trie/search-word test-number-trie number))))))

(deftest test-map-trie
  (testing "Map trie to collection and check if all words are intact"
    (let [words [(generate-one-word) (generate-one-word)]
          numbers [[(generate-one-small-number)] [(generate-one-large-number)]]
          test-trie (trie/insert-word (trie/insert-word (trie/trie-node) (first words)) (last words))
          trie-map (trie/map-trie test-trie)
          test-number-trie (trie/insert-word (trie/insert-word (trie/trie-node) (first numbers)) (last numbers))
          number-map (trie/map-trie test-number-trie)]
      (is (= (set words) (set trie-map)))
      (is (= (set numbers) (set number-map))))))

(deftest test-map-unmap
  (testing "Map trie to collection, then build a new trie from collection, check 2 tries for equality"
    (let [words [(generate-one-word) (generate-one-word)]
          numbers [[(generate-one-small-number)] [(generate-one-large-number)]]
          trie1 (trie/insert-word (trie/insert-word (trie/trie-node) (first words)) (last words))
          trie-map (trie/map-trie trie1)
          trie2 (trie/trie-collection trie-map)
          trie3 (trie/insert-word (trie/insert-word (trie/trie-node) (first numbers)) (last numbers))
          number-map (trie/map-trie trie3)
          trie4 (trie/trie-collection number-map)]
      (is (= true (trie/compare-trie trie1 trie2)))
      (is (= true (trie/compare-trie trie3 trie4))))))

(deftest test-filter-trie
  (testing "Filter trie, then check if trie is filtered"
    (let [word-list (generate-words numwords)
          number-list (generate-numbers numwords)
          pr1 #(= \a %)
          pr2 #(pr1 %)
          pr3 even?
          trie1 (trie/trie-collection word-list)
          trie2 (trie/filter-trie trie1 pr2)
          trie3 (trie/trie-collection number-list)
          trie4 (trie/filter-trie trie3 pr3)
          passed-words (filter pr2 word-list)
          passed-nums (filter pr3 number-list)]
      (is (= (set passed-words) (set (trie/map-trie trie2))))
      (is (= (set passed-nums) (set (trie/map-trie trie4)))))))

(deftest test-fold-trie
  (testing "Folding tries using different functions"
    (let [words (generate-words numwords) ;; Use numwords constant here
          numbers (generate-numbers numwords)
          test-trie (trie/trie-collection words)
          test-number-trie (trie/trie-collection numbers)
          predicate1 #(str %1 "-" %2)
          predicate2 +
          left-fold-res (reduce predicate1 words)
          right-fold-res (reduce predicate1 (reverse words))
          left-fold-number (reduce predicate2 numbers)]
      (is (= (trie/fold-trie-left predicate1 test-trie) left-fold-res))
      (is (= (trie/fold-trie-right predicate1 test-trie) right-fold-res))
      (is (= (trie/fold-trie-left predicate2 test-number-trie) left-fold-number)))))

;; Logic Tests
(deftest logic-merge
  (testing "Check trie union"
    (let [words1 (generate-words numwords) ;; Use numwords constant here
          words2 (generate-words numwords)
          words3 (union (set words1) (set words2))
          A (trie/trie-collection words1)
          B (trie/trie-collection words2)
          C (trie/trie-collection words3)]
      (is (= true (trie/compare-trie C (trie/merge-trie A B)))))))

(deftest logic-intersect
  (testing "Check trie intersection"
    (let [words1 (generate-words numwords) ;; Use numwords constant here
          words2 (generate-words numwords)
          words3 (intersection (set words1) (set words2))
          A (trie/trie-collection words1)
          B (trie/trie-collection words2)
          C (trie/trie-collection words3)]
      (is (= true (trie/compare-trie C (trie/intersect-trie A B)))))))

(deftest logic-subtract
  (testing "Check trie subtraction"
    (let [words1 (generate-words numwords) ;; Use numwords constant here
          words2 (generate-words numwords)
          words3 (difference (set words1) (set words2))
          A (trie/trie-collection words1)
          B (trie/trie-collection words2)
          C (trie/trie-collection words3)]
      (is (= true (trie/compare-trie C (trie/subtract-trie A B)))))))

(deftest logic-xor
  (testing "Check trie XOR"
    (let [words1 (generate-words numwords) ;; Use numwords constant here
          words2 (generate-words numwords)
          words3 (difference (union (set words1) (set words2)) (intersection (set words1) (set words2)))
          A (trie/trie-collection words1)
          B (trie/trie-collection words2)
          C (trie/trie-collection words3)]
      (is (= true (trie/compare-trie C (trie/xor-trie A B)))))))

;; Property Tests
(deftest property-associative
  (testing "Check property A ⋅ (B ⋅ C) = (A ⋅ B) ⋅ C"
    (let [A (trie/trie-collection (generate-words numwords)) ;; Use numwords constant here
          B (trie/trie-collection (generate-words numwords))
          C (trie/trie-collection (generate-words numwords))]
      (is (= true (trie/compare-trie (trie/merge-trie A (trie/merge-trie B C)) (trie/merge-trie (trie/merge-trie A B) C))))
      (is (= true (trie/compare-trie (trie/intersect-trie A (trie/intersect-trie B C)) (trie/intersect-trie (trie/intersect-trie A B) C))))
      (is (= true (trie/compare-trie (trie/xor-trie A (trie/xor-trie B C)) (trie/xor-trie (trie/xor-trie A B) C)))))))

(deftest property-commutative
  (testing "Check property A ⋅ B = B ⋅ A"
    (let [A (trie/trie-collection (generate-words numwords)) ;; Use numwords constant here
          B (trie/trie-collection (generate-words numwords))]
      (is (= true (trie/compare-trie (trie/merge-trie A B) (trie/merge-trie B A))))
      (is (= true (trie/compare-trie (trie/intersect-trie A B) (trie/intersect-trie B A))))
      (is (= true (trie/compare-trie (trie/xor-trie A B) (trie/xor-trie B A)))))))

(deftest property-distributive
  (testing "Check property A & (B | C) = (A & B) | (A & C)"
    (let [A (trie/trie-collection (generate-words numwords)) ;; Use numwords constant here
          B (trie/trie-collection (generate-words numwords))
          C (trie/trie-collection (generate-words numwords))]
      (is (= true (trie/compare-trie (trie/intersect-trie A (trie/merge-trie B C)) (trie/merge-trie (trie/intersect-trie A B) (trie/intersect-trie A C))))))))

  (deftest property-neutral
    (testing "Check property A | O = A, A & O = O"
      (let [A (trie/trie-collection (generate-words numwords)) ;; Use numwords constant here
            O (trie/trie-node)]
        (is (= true (trie/compare-trie (trie/merge-trie A O) A)))
        (is (= true (trie/compare-trie (trie/intersect-trie A O) O))))))

  (deftest property-xor
    (testing "Check properties of XOR: A ⊕ A = O, A ⊕ A ⊕ A = A"
      (let [A (trie/trie-collection (generate-words numwords)) ;; Use numwords constant here
            O (trie/trie-node)]
        (is (= true (trie/compare-trie (trie/xor-trie A A) O)))
        (is (= true (trie/compare-trie (trie/xor-trie (trie/xor-trie A A) A) A))))))
