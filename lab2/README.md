# Лабораторная работа №2

---

**Студент:** Сорокин Артём Николаевич  
**ИСУ:** 367548  
**Группа:** P3306  
**Университет:** НИУ ИТМО  
**Факультет:** Программная инженерия и компьютерная техника  
**Курс:** 3-й курс  

---
# Содержание


1. [Описание лабораторной работы](#описание-лабораторной-работы)  
2. [Описание структуры данных и ограничения](#описание-структуры-данных-и-ограничения)  
   - [Создание бора (node)](#1-создание-бора-node)  
   - [Вставка слова в бор (insert)](#2-вставка-слова-в-бор-insert)  
   - [Удаление слова из бора (remove)](#3-удаление-слова-из-бора-remove)  
   - [Поиск слова (search)](#4-поиск-слова-search)  
   - [Отображение бора (map)](#5-отображение-бора-map)  
   - [Генерация бора (collection)](#6-генерация-бора-collection)  
     - [Обратное отображение](#61-обратное-отображение)  
     - [А что же числа?](#62-а-что-же-числа)  
   - [Фильтрация бора (filter)](#7-фильтрация-бора-filter)  
   - [Свертка бора (leftfold и rightfold)](#8-свертка-бора-leftfold-и-rightfold)  
   - [Сравнение боров (compare)](#9-сравнение-боров-compare)  
   - [Дополнение: бинарная логика](#10-дополнение-бинарная-логика)  
     - [Слияние боров (merge)](#101-слияние-боров-merge)  
     - [Пересечение боров (intersect)](#102-пересечение-боров-intersect)  
     - [Разность боров (subtract)](#103-разность-боров-subtract)  
     - [Исключающее ИЛИ боров (xor)](#104-исключающее-или-боров-xor)  
   - [Моноидальность](#11-моноидальность)  
3. [Тестирование](#тестирование)  


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

## Описание структуры данных и ограничения
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

## 1. **Создание бора $node$**

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

## 2. **Вставка слова в бор $insert$**

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

## 3. Удаление слова из бора $remove$

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

## 4. Поиск слова $search$

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

## 5. Отображение бора $map$
![meme](img/meme.png)

В качестве отображения бора я решил выбрать структуру данных $Set$

Таким образом, бор отображается в набор слов, из которых он состоит:

$map(Trie(S)) = S$

Данная функция работает для любых итерируемых слов.

Исходный код*:

```clojure
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
```


*Как можно заметить, в данном коде для отображения используется общая реализация `map-trie-any`, и частная реализация `map-trie-string`. Это связано с тем, что, хотя строки и коллекции являются итерируемыми объектами, в Clojure строки это `java.lang.String`, а коллекции - `clojure.lang.IPersistentCollection`. Это создает определенные неудобства - что поделать, Java есть Java в любой ее обертке. Всегда можно посмотреть в сторону [Rust](https://github.com/owl-from-hogvarts)

## 6. Генерация бора $collection$

Данная функция позволяет создавать бор сразу из множества слов. Это выглядит так:

$collection(S) = Trie(S)$

Если бор уже существует, данная функция добавляет в него слова из множества $S$:

$collection(Trie(R), S) = Trie(S \cup R)$

Внутри это работает так:

$collection(Trie(R), S) = insert_{i=1...|S|}(Trie(R), word_i \in S)$

Данная функция работает для любых итерируемых слов.

Исходный код:
```clojure
(defn trie-collection
  ([collection] (reduce insert-word (trie-node) collection))
  ([trie collection] (reduce insert-word trie collection)))
```

### 6.1. Обратное отображение

Данная реализация бора позволяет следующий цыганский фокус:

$collection(map(Trie(S))) = Trie(S)$

Таким образом, можно отображать бор в множество слов, и обратно.

А теперь представим следующую ситуацию. Пусть изначально на бор была применена операция $delete$. $delete$ удаляет только пометку о существовании слова, оставляя мусор $C$. Поскольку $map$ оставляет во множестве только валидные слова, имеем:

$map(Trie(S + C)) = S$

Тогда:

$collection(map(Trie(S + C))) = Trie(S)$

Таким образом, бор можно чистить. Это свойство нам понадобится в дальнейшем.

### 6.2. А что же числа?

Числа не являются итерируемыми объектами, поэтому просто так вставить число в бор не получится. Для этого необходимо превратить число в итерируемый объект. Я реализовал это через функцию $transform$:

$N = \sum_{i=0}^{K} {a_i * 10^{K - i}}$

$transform(N) = $ { $a_1, a_2 ... a_K$ } $= word$

Исходный код:
```clojure
(defn number-transform [n]
  (let [digits (loop [num n
                      result []]
                 (if (zero? num)
                   result
                   (recur (quot num 10)
                          (conj result (mod num 10)))))]
    (vec (reverse digits))))
```

Однако это порождает следующую проблему. Пусть $S$ - множество чисел. Тогда:

$map(collection(Trie(transform_i(N_i \in S)))) = S'$

$S \neq S'$, так как $S'$ - множество итерируемых объектов (слов), состоящих из разрядов чисел.

Я не нашел лаконичного способа исправить эту неоднозначность.

## 7. Фильтрация бора $filter$

Данная функция позволяет отфильтровать бор, оставив в нем только слова, соответствующие предикату $P$

Вот как это работает:

$filter(Trie(S), P) = Trie(R)$, где $R = $ { $word \in S : P = True$ }

"Под капотом" $filter$ использует функцию $map$, что осуществляет чистку бора:

$filter(Trie(S + C), P) = Trie(R: C \notin R)$

Исходный код:

```clojure
(defn filter-trie [trie predicate] 
  (trie-collection (filter predicate (map-trie trie))))
```

## 8. Свертка бора $leftfold$ и $rightfold$

Бор можно свернуть, используя функцию свертки $F$.

$leftfold(Trie(S), F) = Result$, где $Result = Any$, зависит от функции свертки $F$

$rightfold(Trie(S), F) = Result$

Исходный код*:

```clojure
(defn fold-trie-left [f trie]
  (let [words (map-trie trie)]
    (reduce f (first words) (rest words))))

(defn fold-trie-right [f trie]
  (let [words (reverse (map-trie trie))]
    (reduce f (first words) (rest words))))
```
*Следует заметить, что работоспособность сверток зависит от того, сочетается ли функция свертки с типом данных бора. Ошибки при свертке - на совести пользователя.

## 9. Сравнение боров $compare$

Свойство, обнаруженное при обратном отображение бора, позволяет нам задать правило сравнения двух боров:

**Бор $Trie_1(S)$ и бор $Trie_2(R)$ равны, если равны их множества слов $S$ и $R$** 

Это позволяет не обращать внимание на мусор $C$, остающийся после удаления слов из бора.

Исходный код:

```clojure
(defn compare-trie [trie1 trie2]
  (= (set (map-trie trie1)) (set (map-trie trie2))))
```

## 10. Дополнение: бинарная логика

### 10.1. Слияние боров $merge$

$merge(Trie(S), Trie(R)) = Trie(S \cup R)$

Исходный код:
```clojure
(defn merge-trie [trie1 trie2]
  (trie-collection trie1 (map-trie trie2))
```

### 10.2. Пересечение боров $intersect$

$intersect(Trie(S), Trie(R)) = Trie(S \cap R)$

Исходный код:
```clojure
(defn intersect-trie [trie1 trie2]
(trie-collection (intersection (set (map-trie trie1)) (set (map-trie trie2)))))
```

### 10.3. Разность боров $subtract$

$subtract(Trie(S), Trie(R)) = Trie(S ∖ R)$

Исходный код:
```clojure
(defn subtract-trie [trie1 trie2]
  (trie-collection (difference (set (map-trie trie1)) (set (map-trie trie2)))))
```

### 10.4. Исключающее ИЛИ боров $xor$

$xor(Trie(S), Trie(R)) = Trie(S \oplus R)$

Исходный код:
```clojure
(defn xor-trie [trie1 trie2]
  (subtract-trie (merge-trie trie1 trie2) (intersect-trie trie1 trie2)))
```

## 11. Моноидальность

Разработанный бор является моноидом, поскольку:
- соблюдается ассоциативность $op(Trie(A), op(Trie(B), Trie(C))) = op(op(Trie(A), Trie(B)), Trie(C))$

- имеется нейтральный элемент $op(Trie(A), Trie(\emptyset)) = Trie(A) | Trie(\emptyset)$ (зависит от операции)

- коммутативность $op(Trie(A), Trie(B)) = op(Trie(B), Trie(A))$

- полиморфизм - опрерации бора работают для любых (итерируемых типов данных)*

*и числа

## Тестирование
Все тесты проходят. Тесты делятся на модульные, логики и свойств. [Ссылка на тесты.](lab2/test/lab2/core-test.clj)
