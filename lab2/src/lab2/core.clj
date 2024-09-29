(ns lab2.core
  (:gen-class))

(defn trie-new
  "Create a new empty Trie."
  []
  {})

(defn trie-contains?
  "Check if a word is present in the Trie."
  [trie word]
  (get-in trie (map keyword word) :end))

(defn trie-insert
  "Add a word to the Trie."
  [trie word]
  (assoc-in trie (map keyword word) :end))

(defn trie-remove
  "Remove a word from the Trie."
  [trie word]
  (update-in trie (map keyword word) (constantly nil)))




(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
