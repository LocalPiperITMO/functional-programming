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

(deftest test-map-trie
  (testing "Map trie to collection and check if all words are intact"
    (let [test-trie (trie/insert-word (trie/insert-word (trie/trie-node) "hello") "world")
          trie-map (trie/map-trie test-trie)]
      (is (= (set ["hello" "world"]) (set trie-map))))))

(deftest test-map-unmap
  (testing "Map trie to collection, then build a new trie from collection, check 2 tries for equalty"
    (let [trie1 (trie/insert-word (trie/insert-word (trie/trie-node) "hello") "world")
          trie-map (trie/map-trie trie1)
          trie2 (trie/trie-collection trie-map)]
      (is (= trie1 trie2))))) ;; TODO: define Trie comparator

(deftest test-filter-trie
  (testing "Filter trie, then check if trie is filtered"
    (let [word-list ["hello" "hola" "hi" "aboba" "amogus" "bubus"]
          pr1 #(= \a %)
          pr2 #(pr1 %)
          trie1 (trie/trie-collection word-list)
          trie2 (trie/filter-trie trie1 pr2)
          passed-words (filter pr2 word-list)
          failed-words (remove pr2 word-list)
          ]
      (is (every? #(trie/search-word trie2 %) passed-words))
      (is (every? #(not (trie/search-word trie2 %)) failed-words)))))

;; Property Tests
