# Лабораторная работа №1

---

**Студент:** Сорокин Артём Николаевич   
**ИСУ:** 367548    
**Группа:** P3306    
**Университет:** НИУ ИТМО  
**Факультет:** Программная инженерия и компьютерная техника  
**Курс:** 3-й курс

---

# Отчет по решениям задач 8 и 23 с Project Euler

В этом проекте представлены решения для задач с сайта Project Euler, реализованные на языке Clojure. Мы рассматриваем несколько подходов к вычислению максимального произведения последовательностей цифр и методы работы с обильными числами. Ниже приведен анализ каждого решения.

## Решения для Задачи 8

Задача 8 заключается в нахождении наибольшего произведения тринадцати последовательных цифр в строке из 1000 цифр. Приведено несколько методов решения задачи, включая рекурсию, хвостовую рекурсию, модульные решения и ленивые коллекции.
Код на языке C++:
```cpp
#include <fstream>
#include <iostream>

using namespace std;

int main() {
  ifstream infile("input8.txt");
  string s;
  infile >> s;
  int n = s.size();
  long long prod = 1, res = 0;
  int prod_len = 0;
  for (int i = 0; i < n; ++i) {
    prod *= (s[i] - '0');
    ++prod_len;
    if (prod == 0) {
      prod = 1;
      prod_len = 0;
      continue;
    }
    while (prod_len > 13) {
      prod /= (s[i - 13] - '0');
      --prod_len;
    }
    if (prod_len == 13) {
      res = (res < prod) ? prod : res;
    }
  }
  cout << res << endl;
  return 0;
}
```

### 1. Простой рекурсивный подход

Эта реализация использует простую рекурсию для вычисления произведения последовательностей:

```clojure
(defn largest-product-in-series-basic-recursion [idx max-prod]
  (if (> idx (- (count problem-8-input) 13))
    max-prod
    (let [current-substr (subs problem-8-input idx (+ idx 13))
          current-prod (product-reduce (map #(Character/digit % 10) current-substr))]
      (largest-product-in-series-basic-recursion (inc idx) (max max-prod current-prod)))))
```

### 2. Хвостовая рекурсия

Для оптимизации используется хвостовая рекурсия:

```clojure
(defn largest-product-in-series-tail-recursion []
  (letfn [(helper [idx max-prod]
            (if (>= idx (- (count problem-8-input) 12))
              max-prod
              (let [current-substr (subs problem-8-input idx (+ idx 13))
                    current-prod (product-reduce (map #(Character/digit % 10) current-substr))]
                (helper (inc idx) (max max-prod current-prod)))))]
    (helper 0 0)))
```

### 3. Модульное решение

Модульный подход организует код для лучшей читаемости и гибкости:

```clojure
(defn generate-subsequences [seq length]
  (map #(take length (drop % seq)) (range (- (count seq) length))))
```

### 4. Использование `map`

Этот модуль применяет функцию `map` для эффективного вычисления произведения:

```clojure
(defn largest-product-in-series-map []
  (let [digits (map #(Character/digit % 10) problem-8-input)]
    (->> (map #(product-apply (take 13 (drop % digits))) (range (- (count digits) 12)))
         (reduce max 0))))
```

### 5. Решение с использованием `loop`

Решение с циклом позволяет избежать рекурсивных вызовов:

```clojure
(defn largest-product-in-series-loop []
  (let [digits (map #(Character/digit % 10) problem-8-input)]
    (loop [idx 0 max-prod 0]
      (if (>= idx (- (count digits) 12))
        max-prod
        (let [current-prod (product-reduce (take 13 (drop idx digits)))]
          (recur (inc idx) (max max-prod current-prod)))))))
```

### 6. Ленивые коллекции

Для обработки длинных последовательностей используется ленивое выполнение:

```clojure
(defn lazy-max-product [digits]
  (let [subsequences (map #(take 13 (drop % digits)) (take (- (count digits) 12) (iterate inc 0)))]
    (reduce max 0 (map product-reduce subsequences))))
```

## Решения для Задачи 23

Задача 23 связана с обильными числами и нахождением суммы всех чисел, которые не могут быть записаны как сумма двух обильных чисел.
Код на языке C++:
```cpp
#include <cmath>
#include <iostream>
#include <vector>
using namespace std;

int divsum(int n) {
  if (n < 2)
    return 0;
  int sum = 1;
  for (int i = 2; i * i <= n; ++i) {
    if (n % i == 0) {
      sum += i;
      if (i != n / i) {
        sum += n / i;
      }
    }
  }
  return sum;
}

void generate(vector<int> &abundantNumbers, int limit) {
  for (int i = 12; i <= limit; ++i)
    if (divsum(i) > i)
      abundantNumbers.push_back(i);
}

int main() {
  const int LIMIT = 28213;
  vector<int> abundantNumbers;
  generate(abundantNumbers, LIMIT);
  vector<bool> canBeWritten(LIMIT + 1, false);

  for (int i = 0; i < abundantNumbers.size(); ++i) {
    for (int j = i; j < abundantNumbers.size(); ++j) {
      long long sum = abundantNumbers[i] + abundantNumbers[j];
      if (sum <= LIMIT) {
        canBeWritten[sum] = true;
      } else {
        break;
      }
    }
  }
  long long res = 0;
  for (long long i = 1; i <= LIMIT; ++i) {
    if (!canBeWritten[i]) {
      res += i;
    }
  }
  cout << res << endl;
  return 0;
}

```
### 1. Хвостовая рекурсия

Для нахождения обильных чисел используется хвостовая рекурсия:

```clojure
(defn find-abundant-numbers-tail-rec [limit i abundants]
  (if (> i limit)
    abundants
    (recur limit (inc i)
           (if (abundant-tail-rec? i)
             (conj abundants i)
             abundants))))
```

### 2. Модульное решение

Модульный подход делает код более структурированным и понятным:

```clojure
(defn non-abundant-sums-modular []
  (let [max-n problem-23-input
        abundant-seq (filter-abundant (generate-sequence max-n))]
    (reduce + (filter #(not (can-be-sum-of-two-abundant? % abundant-set)) (generate-sequence (inc max-n))))))
```

### 3. Использование `map`

Этот стиль позволяет применять функции ко множеству чисел одновременно:

```clojure
(defn can-be-sum-of-two-abundant-map? [n abundant-numbers]
  (some #(let [complement (- n %)]
           (and (pos? complement)
                (contains? abundant-numbers complement)))
        abundant-numbers))
```

### 4. Использование циклов

Циклы используются для итеративного подхода к решению задачи:

```clojure
(defn non-abundant-sums-loop []
  (let [max-n problem-23-input
        abundant-set (abundant-numbers max-n)
        non-abundant-sum (atom 0)]
    (let [abundant-sums (set (for [x abundant-set
                                   y abundant-set]
                               (+ x y)))]
      (doseq [n (range 1 (inc max-n))]
        (when (not (contains? abundant-sums n))
          (swap! non-abundant-sum + n))))
    @non-abundant-sum)))
```

### 5. Ленивые последовательности

Решение с использованием ленивых последовательностей позволяет легко обрабатывать потенциально бесконечные данные:

```clojure
(def abundant-numbers-lazy
  (filter abundant? (rest (iterate inc 1))))
```

## Тестирование
[Ссылка на тесты](/test/lab1/core-test.clj)
Прохождение тестов:
```
lein test lab1.core-test

Ran 2 tests containing 12 assertions.
0 failures, 0 errors.

```
Время выполнения программ:
```
"Elapsed time: 143.629165 msecs"
Problem 8 using Basic Recursion: 23514624000
"Elapsed time: 138.721397 msecs"
Problem 8 using Tail Recursion: 23514624000
"Elapsed time: 52.729894 msecs"
Problem 8 using Modular Realization: 23514624000
"Elapsed time: 50.318837 msecs"
Problem 8 using Map: 23514624000
"Elapsed time: 125.844356 msecs"
Problem 8 using Loop: 23514624000
"Elapsed time: 48.281642 msecs"
Problem 8 using Lazy Collections: 23514624000
"Elapsed time: 0.008635 msecs"
Problem 23 using Basic Recursion: 4179871
"Elapsed time: 24640.391307 msecs"
Problem 23 using Tail Recursion: 4179871
"Elapsed time: 25325.994854 msecs"
Problem 23 using Modular Realization: 4179871
"Elapsed time: 23472.976114 msecs"
Problem 23 using Map: 4179871
"Elapsed time: 61029.236202 msecs"
Problem 23 using Loop: 4179871
"Elapsed time: 23742.797796 msecs"
Problem 23 using Lazy Collections: 4179871
```
## Заключение

Представленные решения демонстрируют различные подходы к решению задач с использованием функционального программирования на Clojure. Эти примеры показывают, как рекурсия, циклы и функции высшего порядка могут эффективно решать математические задачи.
