(ns lab2.core
  (:gen-class) 
  (:require [clojure.set :refer [difference intersection]]
            [clojure.string :as string]))

(defn trie-node
  ([] {:value nil :children {}})
  ([value] {:value value :children {}}))

(defn insert [node value]
  (if (empty? value)
    (assoc node :is-end true)
    (let [curr (first value)
          next (rest value)
          child-node (if (contains? (:children node) curr)
                       (get (:children node) curr)
                       (trie-node curr))]
      (assoc node :children (assoc (:children node) curr (insert child-node next))))))

  (defn insert-word [root word]
    (let [existing-node (if (contains? (:children root) (first word))
                          (get (:children root) (first word))
                          (trie-node (first word)))]
      (assoc root :children (assoc (:children root) (first word) (insert existing-node (rest word))))))

(defn trie-collection 
  ([collection] (reduce insert-word (trie-node) collection))
  ([trie collection] (reduce insert-word trie collection)))

(defn search [node value]
  (if (empty? value)
    (if (contains? node :is-end)
      (:is-end node)
      false)
    (let [curr (first value)
          next (rest value)
          child-node (if (contains? (:children node) curr)
                       (get (:children node) curr)
                       false)]
      (if (false? child-node)
        false
        (search child-node next)))))

(defn search-word [root word]
  (let [existing-node (if (contains? (:children root) (first word))
                        (get (:children root) (first word))
                        false)]
    (if (false? existing-node)
      false
      (search existing-node (rest word)))))

(defn seek-and-destroy [node word]
  (if (empty? word)
    (assoc node :is-end false)
    (let [curr (first word)
          next (rest word)
          child-node (get-in node [:children curr])]
      (if child-node
        (assoc node :children (assoc (:children node) curr (seek-and-destroy child-node next)))
        node))))

  (defn remove-word [root word]
    (if (contains? (:children root) (first word))
      (let [new-children (assoc (:children root) (first word) (seek-and-destroy (get (:children root) (first word)) (rest word)))]
        (assoc root :children new-children))
      root))

(defn map-trie [trie]
  (letfn [(traverse [node acc res]
            (if (empty? (:children node))
              (if (:is-end node)
                (conj res (string/join acc))
                res)
              (reduce (fn [new-res [char child]]
                        (traverse child (conj acc char) new-res))
                      (if (:is-end node)
                        (conj res (string/join acc))
                        res)
                      (:children node))))]
    (traverse trie [] [])))


(defn filter-trie [trie predicate] 
  (trie-collection (filter predicate (map-trie trie))))

(defn merge-trie [trie1 trie2]
  (trie-collection trie1 (map-trie trie2))
  )
(defn intersect-trie [trie1 trie2]
(trie-collection (intersection (set (map-trie trie1)) (set (map-trie trie2)))))

(defn subtract-trie [trie1 trie2]
  (trie-collection (difference (set (map-trie trie1)) (set (map-trie trie2)))))

(defn xor-trie [trie1 trie2]
  (subtract-trie (merge-trie trie1 trie2) (intersect-trie trie1 trie2)))

(defn fold-trie-left [f trie]
  (let [words (map-trie trie)]
    (reduce f (first words) (rest words))))

(defn fold-trie-right [f trie]
  (let [words (reverse (map-trie trie))]
    (reduce f (first words) (rest words))))

(defn compare-trie [trie1 trie2]
  (= (set (map-trie trie1)) (set (map-trie trie2))))

  (defn -main
    "I don't do a whole lot ... yet."
    [& args]
    (insert-word (trie-node) "abc")
    (insert-word (trie-node) "bcd"))

