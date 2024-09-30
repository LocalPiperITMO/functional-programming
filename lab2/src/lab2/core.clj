(ns lab2.core
  (:gen-class))

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

  (defn -main
    "I don't do a whole lot ... yet."
    [& args]
    (insert-word (trie-node) "abc")
    (insert-word (trie-node) "bcd"))

