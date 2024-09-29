(ns lab2.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [lab2.core :as trie]))

;; Unit Tests
(deftest test-add-word
  (testing "Add word and check if it's present"
    (is (= true (trie/trie-contains? (trie/trie-insert (trie/trie-new) "hello") "hello")))))

(deftest test-add-multiple-words
  (testing "Add multiple words and check if they are present"
    (is (every? #(trie/trie-contains? (trie/trie-insert (trie/trie-new) %) %)
                ["hello" "world"]))))

(deftest test-remove-word
  (testing "Add and Remove word"
    (is (= false (trie/trie-contains? (trie/trie-remove (trie/trie-insert (trie/trie-new) "hello") "hello") "hello")))))


;; Property Tests
(deftest test-associative-property-words
  (testing "Associative property test (words)"
    (is (= true false))))

(deftest test-neutral-element-words
  (testing "Neutral element property test (words)"
    (is (= (trie/trie-insert (trie/trie-new) "") (trie/trie-new)))))


;; Run all tests including unit and property tests
(deftest all-tests
  (test-add-word)
  (test-add-multiple-words)
  (test-remove-word)
  (test-associative-property-words)
  (test-neutral-element-words))
