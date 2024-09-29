(ns lab2.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [lab2.core :as trie]))

;; Unit Tests
(deftest test-add-word
  (testing "Add word and check if it's present"
    (let [test-trie (trie/insert-word (trie/trie-node) "hello")]
      (is (= true (trie/search-word test-trie "hello"))))
    ))

(deftest test-add-multiple-words
  (testing "Add multiple words and check if they are present"
    (let [test-trie (trie/insert-word (trie/insert-word (trie/trie-node) "hello") "world")]
      (is (every? #(trie/search-word test-trie %) ["hello" "world"]) ))))

(deftest test-remove-word
  (testing "Add and Remove word"
    (let [test-trie (trie/remove-word (trie/insert-word (trie/trie-node) "hello") "hello")]
      (is (= false (trie/search-word test-trie "hello"))))))



;; Property Tests
