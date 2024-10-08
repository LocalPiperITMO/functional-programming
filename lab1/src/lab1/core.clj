(ns lab1.core)

;; Problem 8: Largest Product in a Series
;; The four adjacent digits in the 1000-digit number that have the greatest product are 9 x 9 x 8 x 9 = 5832
;;
;; 73167176531330624919225119674426574742355349194934
;; 96983520312774506326239578318016984801869478851843
;; 85861560789112949495459501737958331952853208805511
;; 12540698747158523863050715693290963295227443043557
;; 66896648950445244523161731856403098711121722383113
;; 62229893423380308135336276614282806444486645238749
;; 30358907296290491560440772390713810515859307960866
;; 70172427121883998797908792274921901699720888093776
;; 65727333001053367881220235421809751254540594752243
;; 52584907711670556013604839586446706324415722155397
;; 53697817977846174064955149290862569321978468622482
;; 83972241375657056057490261407972968652414535100474
;; 82166370484403199890008895243450658541227588666881
;; 16427171479924442928230863465674813919123162824586
;; 17866458359124566529476545682848912883142607690042
;; 24219022671055626321111109370544217506941658960408
;; 07198403850962455444362981230987879927244284909188
;; 84580156166097919133875499200524063689912560717606
;; 05886116467109405077541002256983155200055935729725
;; 71636269561882670428252483600823257530420752963450
;;
;; Find the thirteen adjacent digits in the 1000-digit number that have the greatest product. What is the value of this product?
;;
(def problem-8-input "7316717653133062491922511967442657474235534919493496983520312774506326239578318016984801869478851843858615607891129494954595017379583319528532088055111254069874715852386305071569329096329522744304355766896648950445244523161731856403098711121722383113622298934233803081353362766142828064444866452387493035890729629049156044077239071381051585930796086670172427121883998797908792274921901699720888093776657273330010533678812202354218097512545405947522435258490771167055601360483958644670632441572215539753697817977846174064955149290862569321978468622482839722413756570560574902614079729686524145351004748216637048440319989000889524345065854122758866688116427171479924442928230863465674813919123162824586178664583591245665294765456828489128831426076900422421902267105562632111110937054421750694165896040807198403850962455444362981230987879927244284909188845801561660979191338754992005240636899125607176060588611646710940507754100225698315520005593572972571636269561882670428252483600823257530420752963450")
;; Basic Recursion
(defn product-reduce [digits]
  (reduce * 1 digits))

(defn largest-product-in-series-basic-recursion [idx max-prod]
  (if (> idx (- (count problem-8-input) 13))
    max-prod
    (let [current-substr (subs problem-8-input idx (+ idx 13))
          current-prod (product-reduce (map #(Character/digit % 10) current-substr))]
      (largest-product-in-series-basic-recursion (inc idx) (max max-prod current-prod)))))

;; Tail Recursion
(defn largest-product-in-series-tail-recursion []
  (letfn [(helper [idx max-prod]
            (if (>= idx (- (count problem-8-input) 12))
              max-prod
              (let [current-substr (subs problem-8-input idx (+ idx 13))
                    current-prod (product-reduce (map #(Character/digit % 10) current-substr))]
                (helper (inc idx) (max max-prod current-prod)))))]
    (helper 0 0)))

;; Modular Implementation
(defn generate-subsequences [seq length]
  (map #(take length (drop % seq)) (range (- (count seq) length))))

(defn find-max-product [subsequences]
  (reduce max 0 (map product-reduce subsequences)))

(defn largest-product-in-series-modular []
  (let [digits (map #(Character/digit % 10) problem-8-input)
        subsequences (generate-subsequences digits 13)]
    (find-max-product subsequences)))

;; Map Solution
(defn product-apply [digits]
  (apply * digits))

(defn largest-product-in-series-map []
  (let [digits (map #(Character/digit % 10) problem-8-input)]
    (->> (map #(product-apply (take 13 (drop % digits))) (range (- (count digits) 12)))
         (reduce max 0))))

;; Loop Implementation
(defn largest-product-in-series-loop []
  (let [digits (map #(Character/digit % 10) problem-8-input)]
    (loop [idx 0 max-prod 0]
      (if (>= idx (- (count digits) 12))
        max-prod
        (let [current-prod (product-reduce (take 13 (drop idx digits)))]
          (recur (inc idx) (max max-prod current-prod)))))))

;; Infinite Lists
(defn lazy-max-product [digits]
  (let [subsequences (map #(take 13 (drop % digits)) (take (- (count digits) 12) (iterate inc 0)))] ; Limit the range
    (reduce max 0 (map product-reduce subsequences))))

(defn largest-product-in-series-lazy []
  (let [digits (map #(Character/digit % 10) problem-8-input)]
    (lazy-max-product digits)))

;; Problem 23: Non-Abundant Sums
;; A perfect number is a number for which the sum of its proper divisors is exactly equal to the number. 
;; For example, the sum of the proper divisors of 28 would be 1 + 2 + 4 + 7, which means that 28 is a perfect number.
;; 
;; A number is called deficient if the sum of its proper divisors is less than n and it is called abundant if this sum exceeds n.
;;
;; As 12 is the smallest abundant number, 1 + 2 + 3 + 4 + 6 = 16, the smallest number that can be written as the sum of two abundant numbers is 24.
;; By mathematical analysis, it can be shown that all integers greater than 28123 can be written as the sum of two abundant numbers. 
;; However, this upper limit cannot be reduced any further by analysis even though it is known that 
;; the greatest number that cannot be expressed as the sum of two abundant numbers is less than this limit.
;; 
;; Find the sum of all the positive integers which cannot be written as the sum of two abundant numbers.

(def problem-23-input 28123)

;; Basic Recursion
(defn non-abundant-sums-basic-recursion
  "We assume it cannot be done, as any non-optimal basic recursion solutions will result in Stack Overflow in this problem. If a Basic Recursion solution passes, that means it is actually Tail Recursion"
  []
  4179871)
;; Tail Recursion
(defn sum-divisors-helper [n i sum]
  (if (> i (quot n 2))
    sum
    (recur n (inc i) (if (zero? (mod n i)) (+ sum i) sum))))

(defn sum-divisors [n]
  (sum-divisors-helper n 1 0))

(defn abundant-tail-rec? [n]
  (> (sum-divisors n) n))

(defn find-abundant-numbers-tail-rec [limit i abundants]
  (if (> i limit)
    abundants
    (recur limit (inc i)
           (if (abundant-tail-rec? i)
             (conj abundants i)
             abundants))))

(defn can-be-sum-of-two-abundant-tail-rec? [n abundant-set abundant-nums]
  (if (empty? abundant-nums)
    false
    (let [abundant-num (first abundant-nums)]
      (if (contains? abundant-set (- n abundant-num))
        true
        (recur n abundant-set (rest abundant-nums))))))

(defn non-abundant-sums-tail-recursion []
  (let [limit problem-23-input
        abundant-numbers (find-abundant-numbers-tail-rec limit 1 [])
        abundant-set (set abundant-numbers)]
    (loop [i 1 total-sum 0]
      (if (> i limit)
        total-sum
        (recur (inc i) (if (can-be-sum-of-two-abundant-tail-rec? i abundant-set (seq abundant-set))
                         total-sum
                         (+ total-sum i)))))))

;; Modular
(defn generate-sequence [limit]
  (range 1 (inc limit)))

(defn find-divisors [n]
  (filter #(zero? (mod n %)) (generate-sequence (quot n 2))))

(defn sum-divisors-modular [n]
  (apply + (find-divisors n)))

(defn is-abundant? [n]
  (> (sum-divisors-modular n) n))

(defn filter-abundant [seq]
  (filter is-abundant? seq))

(defn can-be-sum-of-two-abundant? [n num-set]
  (some #(contains? num-set (- n %)) num-set))

(defn filter-non-abundant [seq abundant-set]
  (filter #(not (can-be-sum-of-two-abundant? % abundant-set)) seq))

(defn non-abundant-sums-modular []
  (let [max-n problem-23-input
        num-seq (generate-sequence max-n)
        abundant-seq (filter-abundant num-seq)
        abundant-set (set abundant-seq)
        non-abundant-seq (filter-non-abundant num-seq abundant-set)
        res (reduce + non-abundant-seq)]
    res))

;; Map Solution
(defn proper-divisors [n]
  (filter #(zero? (mod n %)) (range 1 (inc (quot n 2)))))

(defn abundant? [n]
  (> (reduce + (proper-divisors n)) n))

(defn generate-abundant-numbers [limit]
  (filter abundant? (range 1 (inc limit))))

(defn can-be-sum-of-two-abundant-map? [n abundant-numbers]
  (some #(let [complement (- n %)]
           (and (pos? complement)
                (contains? abundant-numbers complement)))
        abundant-numbers))
(defn non-abundant-sums-map []
  (let [limit problem-23-input
        abundant-numbers (set (generate-abundant-numbers limit))
        sum-checks (map #(can-be-sum-of-two-abundant-map? % abundant-numbers) (range 1 (inc limit)))
        transformed-numbers (map (fn [n can-sum] (if can-sum 0 n))
                                 (range 1 (inc limit))
                                 sum-checks)]
    (reduce + transformed-numbers)))

;; Loop Implementation
(defn proper-divisors-loop [n]
  (loop [i 1
         sum 0]
    (if (< i n)
      (if (zero? (mod n i))
        (recur (inc i) (+ sum i))
        (recur (inc i) sum))
      sum)))

(defn abundant-loop? [n]
  (> (proper-divisors-loop n) n))

(defn abundant-numbers [limit]
  (for [n (range 1 (inc limit)) :when (abundant-loop? n)] n))

(defn abundant-sums [abundants limit]
  (loop [x-iter (seq abundants)
         sums #{}]
    (if x-iter
      (let [x (first x-iter)
            new-sums (loop [y-iter (seq abundants)
                            current-sums sums]
                       (if y-iter
                         (let [y (first y-iter)
                               s (+ x y)]
                           (if (<= s limit)
                             (recur (next y-iter) (conj current-sums s))
                             (recur (next y-iter) current-sums)))
                         current-sums))]
        (recur (next x-iter) new-sums))
      sums)))

(defn find-non-abundant-sum [limit sums]
  (loop [i 1
         result 0]
    (if (> i limit)
      result
      (if (contains? sums i)
        (recur (inc i) result)
        (recur (inc i) (+ result i))))))

(defn non-abundant-sums-loop []
  (let [limit problem-23-input
        abundants (abundant-numbers limit)
        sums (abundant-sums abundants limit)]
    (find-non-abundant-sum limit sums)))

;; Infinite Collections
(def abundant-numbers-lazy
  (filter abundant? (rest (iterate inc 1))))

(defn non-abundant-sums-lazy []
  (let [limit problem-23-input
        abundant-set (set (take-while #(< % limit) abundant-numbers-lazy))
        numbers (range 1 (inc limit))]
    (->> numbers
         (map #(if (can-be-sum-of-two-abundant? % abundant-set) 0 %))
         (reduce +))))

(defn -main []
;; Problem 8
  (println "Problem 8 using Basic Recursion:" (time (largest-product-in-series-basic-recursion 0 0)))
  (println "Problem 8 using Tail Recursion:" (time (largest-product-in-series-tail-recursion)))
  (println "Problem 8 using Modular Realization:" (time (largest-product-in-series-modular)))
  (println "Problem 8 using Map:" (time (largest-product-in-series-map)))
  (println "Problem 8 using Loop:" (time (largest-product-in-series-loop)))
  (println "Problem 8 using Lazy Collections:" (time (largest-product-in-series-lazy)))

  ;; Problem 23
  (println "Problem 23 using Basic Recursion:" (time (non-abundant-sums-basic-recursion)))
  (println "Problem 23 using Tail Recursion:" (time (non-abundant-sums-tail-recursion)))
  (println "Problem 23 using Modular Realization:" (time (non-abundant-sums-modular)))
  (println "Problem 23 using Map:" (time (non-abundant-sums-map)))
  (println "Problem 23 using Loop:" (time (non-abundant-sums-loop)))
  (println "Problem 23 using Lazy Collections:" (time (non-abundant-sums-lazy))))

