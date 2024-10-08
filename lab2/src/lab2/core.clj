(ns lab2.core
  (:gen-class) 
  (:require [clojure.set :refer [difference intersection]]
            [clojure.string :as string]))


(defn number-transform [n]
  (let [digits (loop [num n
                      result []]
                 (if (zero? num)
                   result
                   (recur (quot num 10)
                          (conj result (mod num 10)))))]
    (vec (reverse digits))))



;; Time to suffer with abstract Trie API
;; insert type-independent code here:

(defn trie-node
  ([] {:value nil :children {}})
  ([value] {:value value :children {}}))

(defprotocol Partition
  (get-first [value])
  (get-rest [value])
  (check-empty? [value])
 )

(extend-protocol Partition
  clojure.lang.IPersistentCollection
  (get-first [value] (first value))
  (get-rest [value] (rest value))
  (check-empty? [value] (empty? value))
  java.lang.String
  (get-first [value] (first value))
  (get-rest [value] (rest value))
  (check-empty? [value] (empty? value))
  )

(defn insert [node value]
  (if (check-empty? value)
    (assoc node :is-end true)
    (let [curr (get-first value)
          next (get-rest value)
          child-node (if (contains? (:children node) curr)
                       (get (:children node) curr)
                       (trie-node curr))]
      (assoc node :children (assoc (:children node) curr (insert child-node next))))))

(defn insert-word [root word]
  (let [new-word (if (= java.lang.Long (type word))
                   (number-transform word)
                   word)
        existing-node (if (contains? (:children root) (get-first new-word))
                        (get (:children root) (get-first new-word))
                        (trie-node (get-first new-word)))]
    (assoc root :children (assoc (:children root) (get-first new-word) (insert existing-node (get-rest new-word))))))


(defn trie-collection
  ([collection] (reduce insert-word (trie-node) collection))
  ([trie collection] (reduce insert-word trie collection)))

(defn search [node value]
  (if (check-empty? value)
    (if (contains? node :is-end)
      (:is-end node)
      false)
    (let [curr (get-first value)
          next (get-rest value)
          child-node (if (contains? (:children node) curr)
                       (get (:children node) curr)
                       false)]
      (if (false? child-node)
        false
        (search child-node next)))))

(defn search-word [root word]
  (let [new-word (if (= java.lang.Long (type word))
                   (number-transform word)
                   word)
        existing-node (if (contains? (:children root) (get-first new-word))
                        (get (:children root) (get-first new-word))
                        false)]
    (if (false? existing-node)
      false
      (search existing-node (get-rest new-word)))))

(defn seek-and-destroy [node word]
  (if (check-empty? word)
    (assoc node :is-end false)
    (let [curr (get-first word)
          next (get-rest word)
          child-node (get-in node [:children curr])]
      (if child-node
        (assoc node :children (assoc (:children node) curr (seek-and-destroy child-node next)))
        node))))

  (defn remove-word [root word]
    (let [new-word (if (= java.lang.Long (type word))
                     (number-transform word)
                     word)]
      (if (contains? (:children root) (get-first new-word))
        (let [new-children (assoc (:children root) (get-first new-word) (seek-and-destroy (get (:children root) (get-first new-word)) (get-rest new-word)))]
          (assoc root :children new-children))
        root)
      )
    )

(defn map-trie-string [trie]
  (letfn [(traverse [node acc res]
            (if (check-empty? (:children node))
              (if (:is-end node)
                (conj res (string/join acc))
                res)
              (reduce (fn [new-res [char child]]
                        (traverse child (conj acc char) new-res))
                      (if (:is-end node)
                        (conj res (string/join acc))
                        res)
                      (:children node))))]
    (traverse trie [] []))
  )


(defn map-trie-any [trie]
  (letfn [(traverse [node acc res]
            (if (check-empty? (:children node))
              (if (:is-end node)
                (conj res (vec acc))
                res)
              (reduce (fn [new-res [key child]]
                        (traverse child (conj acc key) new-res))
                      (if (:is-end node)
                        (conj res (vec acc))
                        res)
                      (:children node))))]
    (traverse trie [] [])))

    (defn map-trie [trie]
      (if (empty? (:children trie))
        []
        (let [first-key-type (type (-> trie :children keys first))]
          (cond
            (= java.lang.Character first-key-type) (map-trie-string trie)
            :else (map-trie-any trie)))))


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

  #_{:clj-kondo/ignore [:unused-binding]}
  (defn -main
    "I don't do a whole lot ... yet."
    [& args]
    (insert-word (trie-node) "abc")
    (insert-word (trie-node) "bcd"))

