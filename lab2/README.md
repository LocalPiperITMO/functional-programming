# Лабораторная работа №2

---

**Студент:** Сорокин Артём Николаевич  
**ИСУ:** 367548  
**Группа:** P3306  
**Университет:** НИУ ИТМО  
**Факультет:** Программная инженерия и компьютерная техника  
**Курс:** 3-й курс  

---

## Описание лабораторной работы

Цель: освоиться с построением пользовательских типов данных, полиморфизмом, рекурсивными алгоритмами и средствами тестирования (unit testing, property-based testing).
В рамках лабораторной работы вам предлагается реализовать одну из предложенных классических структур данных (список, дерево, бинарное дерево, hashmap, граф...).
Требования:
1. Функции:

- добавление и удаление элементов;
- фильтрация;
- отображение (map);
- свертки (левая и правая);
- структура должна быть моноидом.


2. Структуры данных должны быть неизменяемыми.
3. Библиотека должна быть протестирована в рамках unit testing.
4. Библиотека должна быть протестирована в рамках property-based тестирования (как минимум 3 свойства, включая свойства моноида).
5. Структура должна быть полиморфной.
6. Требуется использовать идиоматичный для технологии стиль программирования. Примечание: некоторые языки позволяют получить большую часть API через реализацию небольшого интерфейса. Так как лабораторная работа про ФП, а не про экосистему языка -- необходимо реализовать их вручную и по возможности -- обеспечить совместимость.

Структура данных - бор (префиксное дерево) с использованием словаря (map).
Язык - Clojure.

### Описание структуры данных и ограничения
Бор - префиксное дерево (trie), предназначенное для хранения слов (word).
Далее будем использовать следующие обозначения:

$Trie(S)$ - бор, содержащий множество слов $S$.

Каждая вершина бора представляет из себя словарь $TrieNode(value, is-end, children)$, содержащий следующие пары ключ-значение:

Любая $TrieNode(value, is-end, children)$ может рассматриваться как $Trie(S)$.

$value:Any$ - значение вершины, может быть любым объектом.

Вершины, находящиеся на одном уровне и имеющие одинаковый $value$, объединяются в одну.

$is-end:Boolean$ - флаг, обозначающий конец слова $word$.

$children: Map(key:Any - value:TrieNode ...)$ - словарь, где ключом является значение дочерней вершины, а значением - следующая вершина. Таких пар может быть несколько.

$S$ = { $\text{word}_1, \text{word}_2, \ldots$ } - множество слов.

$word$ = { $\text{value}_1, \text{value}_2, \ldots \text{value(is-end=True)}_n$ } - слово.

$word$ является итерируемым объектом.

### 1. **Создание бора $node$**

Создание бора происходит следующим образом:
   
$node() = Trie(\emptyset)$

Данная функция создает пустой бор. Существует также функция:

$node(value) = TrieNode(value)$

Она используется для создания промежуточных вершин при вставке слова $word$ в бор.

Исходный код:
```clojure
(defn trie-node
  ([] {:value nil :children {}})
  ([value] {:value value :children {}}))
```

### 2. **Вставка слова в бор $insert$**

Вставка слова в бор происходит следующим образом:

$insert(Trie(S), word) = Trie(S + word)$

Если $word \in S$, то:

$insert(Trie(S), word) = Trie(S)$

Слово вставляется рекурсивно, последняя вершина $TrieNode(Any, is-end=True, Any)$

Данная функция работает для любых итерируемых слов.

Исходный код:

```clojure
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
```

### 3. Удаление слова из бора $remove$

Удаление слова из бора происходит следующим образом:

$remove(Trie(S), word) = Trie(S ∖ word + C)$

Данная функция рекурсивно ищет необходимое слово в боре, и при нахождении, изменяет значение $is-end$ на $False$.

В данном случае, $C$ - "мусор", остаточные $TreeNode$, ранее принадлежавшие удаленному слову $word$. Удалять их нельзя, так как они могут принадлежать другим словам бора.

Если $word \notin S$, то:

$remove(Trie(S), word) = Trie(S)$

Данная функция работает для любых итерируемых слов.

Исходный код:
```clojure
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
```

### 4. Поиск слова $search$

Поиск слов в боре происходит следующим образом:

$search(Trie(S), word) = Result$, где $Result = True$ при $word \in S$ и $Result = False$ иначе.

Слово ищется рекурсивно, если последняя вершина $TrieNode(Any, is-end=True, Any)$, то слово найдено.

Данная функция работает для любых итерируемых слов.

Исходный код:

```clojure
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
```

### 5. Отображение бора $map$
![meme](lab2/img/meme.png)

 ```
   - Для обеспечения работы с различными типами данных в TRIE определен протокол `Partition`, который определяет операции для доступа к первому элементу, оставшимся элементам и проверки на пустоту.
```clojure
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
  java.lang.Long 
  (get-first [value] (if (zero? value) 0 (- (long (first (str value))) 48)))
  (get-rest [value] (if (< value 10) 0 (Long/parseLong (subs (str value) 1)))) 
  (check-empty? [value] (zero? value)))
```
2. **Операции над TRIE:**
   - `insert-word` вставляет слово в TRIE.
```clojure
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
    (let [existing-node (if (contains? (:children root) (get-first word))
                          (get (:children root) (get-first word))
                          (trie-node (get-first word)))]
      (assoc root :children (assoc (:children root) (get-first word) (insert existing-node (get-rest word))))))
```
   - `search-word` выполняет поиск слова в TRIE.
```clojure
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
  (let [existing-node (if (contains? (:children root) (get-first word))
                        (get (:children root) (get-first word))
                        false)]
    (if (false? existing-node)
      false
      (search existing-node (get-rest word)))))
```
   - `remove-word` удаляет слово из TRIE.
```clojure
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
    (if (contains? (:children root) (get-first word))
      (let [new-children (assoc (:children root) (get-first word) (seek-and-destroy (get (:children root) (get-first word)) (get-rest word)))]
        (assoc root :children new-children))
      root))
```

3. **Манипуляции с TRIE:**
      - `map-trie` преобразует TRIE в различные типы данных в зависимости от типов ключей.
```clojure
(defn map-trie-string [trie]
  (letfn [(traverse [node acc res]
            (if (check-empty? (:children node))
              (if (:is-end node)
                (conj res (string/join acc)) ;; type-dependent code
                res)
              (reduce (fn [new-res [char child]]
                        (traverse child (conj acc char) new-res)) ;; type-dependent code
                      (if (:is-end node)
                        (conj res (string/join acc)) ;; type-dependent code
                        res)
                      (:children node))))]
    (traverse trie [] []))
  )

(defn map-trie-number [trie]
  (letfn [(traverse [node acc res]
            (if (check-empty? (:children node))
              (if (:is-end node)
                (conj res (Long/parseLong (apply str acc)))
                res)
              (reduce (fn [new-res [char child]]
                        (traverse child (conj acc char) new-res))
                      (if (:is-end node)
                        (conj res (Long/parseLong (apply str acc)))
                        res)
                      (:children node))))]
    (traverse trie [] [])))


  (defn map-trie-any [trie]
    (letfn [(traverse [node acc res]
              (if (check-empty? (:children node))
                (if (:is-end node)
                  (conj res (:value node))
                  res)
                (reduce (fn [new-res [char child]]
                          (traverse child (conj acc char) new-res))
                        (if (:is-end node)
                          (conj res (:value node))
                          res)
                        (:children node))))]
      (traverse trie [] [])))


    (defn map-trie [trie]
      (if (empty? (:children trie))
        []
        (let [first-key-type (type (-> trie :children keys first))]
          (cond
            (= java.lang.Character first-key-type) (map-trie-string trie)
            (= java.lang.Long first-key-type) (map-trie-number trie)
            :else (map-trie-any trie)))))
```
   - Фильтрация `filter-trie`, объединение `merge-trie`, пересечение `intersect-trie`, вычитание `subtract-trie`, исключающее ИЛИ `xor-trie`.
```clojure
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
```
   - Сворачивание элементов `fold-trie-left` и `fold-trie-right`.
```clojure
(defn fold-trie-left [f trie]
  (let [words (map-trie trie)]
    (reduce f (get-first words) (get-rest words))))

(defn fold-trie-right [f trie]
  (let [words (reverse (map-trie trie))]
    (reduce f (get-first words) (get-rest words))))
```
   - Сравнение двух TRIE `compare-trie`.
```clojure
(defn compare-trie [trie1 trie2]
  (= (set (map-trie trie1)) (set (map-trie trie2))))
```

### Анализ функций и структур данных
- **Insert, Search, Remove:** Вставка, поиск и удаление реализованы эффективно, используя рекурсивный подход.
- **Map-trie:** Функции `map-trie-string`, `map-trie-number`, `map-trie-any` полезны для преобразования TRIE в различные типы данных в зависимости от типов ключей узлов.
- **Merge, Intersect, Subtract, XOR:** Операции склейки, пересечения, вычитания и исключающего ИЛИ реализованы для работы с TRIE.
- **Fold-Trie:** Функции `fold-trie-left` и `fold-trie-right` проводят операции свертки по элементам дерева.
- **Compare-trie:** В функции `compare-trie` осуществляется сравнение двух TRIE на идентичность.

### Тестирование
Все тесты проходят. Тесты делятся на модульные, логики и свойств. [Ссылка на тесты.](lab2/test/lab2/core-test.clj)
