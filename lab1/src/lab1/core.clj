(ns lab1.core)

;; Problem 8
;;
;;
(def problem-8-input "7316717653133062491922511967442657474235534919493496983520312774506326239578318016984801869478851843858615607891129494954595017379583319528532088055111254069874715852386305071569329096329522744304355766896648950445244523161731856403098711121722383113622298934233803081353362766142828064444866452387493035890729629049156044077239071381051585930796086670172427121883998797908792274921901699720888093776657273330010533678812202354218097512545405947522435258490771167055601360483958644670632441572215539753697817977846174064955149290862569321978468622482839722413756570560574902614079729686524145351004748216637048440319989000889524345065854122758866688116427171479924442928230863465674813919123162824586178664583591245665294765456828489128831426076900422421902267105562632111110937054421750694165896040807198403850962455444362981230987879927244284909188845801561660979191338754992005240636899125607176060588611646710940507754100225698315520005593572972571636269561882670428252483600823257530420752963450")

;; Basic Recursion Solution
(defn product [digits]
  (if (empty? digits)
    1
    (* (first digits) (product (rest digits)))))

(defn largest-product-in-series-basic-recursion-rec [n idx max-prod]
  (if (> idx (- (count n) 13))
    max-prod
    (let [substr (map #(Character/digit % 10) (subs n idx (+ idx 13)))
          current-prod (product substr)]
      (largest-product-in-series-basic-recursion-rec n (inc idx) (max max-prod current-prod)))))

(defn largest-product-in-series-basic-recursion []
  (largest-product-in-series-basic-recursion-rec problem-8-input 0 0))
(defn largest-product-in-series-tail-recursion [n acc]
  0)
(defn largest-product-in-series-modular [limit]
  0)
;; Map Solution
(defn product-apply [digits]
  (apply * digits))

(defn largest-product-in-series-map []
  (let [digits (map #(- (int %) (int \0)) problem-8-input)]
    (->> (map #(product-apply (take 13 (drop % digits))) (range (- (count digits) 12)))
         (reduce max 0))))
(defn largest-product-in-series-loop [limit]
  0)
(defn largest-product-in-series-lazy [limit]
  0)

;; Problem 23
(defn non-abundant-sums-basic-recursion [n]
  0)
(defn non-abundant-sums-tail-recursion [n acc]
  0)
(defn non-abundant-sums-modular [limit]
  0)
(defn non-abundant-sums-map [limit]
  0)
(defn non-abundant-sums-loop [limit]
  0)
(defn non-abundant-sums-lazy [limit]
  0)

(defn -main []
  ;; Problem 8
  (println "Problem 8 using Basic Recursion:" (largest-product-in-series-basic-recursion))
  (println "Problem 8 using Tail Recursion:" (largest-product-in-series-tail-recursion 0 0))
  (println "Problem 8 using Modular Realization:" (largest-product-in-series-modular 0))
  (println "Problem 8 using Map:" (largest-product-in-series-map))
  (println "Problem 8 using Loop:" (largest-product-in-series-loop 0))
  (println "Problem 8 using Lazy Collections:" (largest-product-in-series-lazy 0))

  ;; Problem 23
  (println "Problem 23 using Basic Recursion:" (non-abundant-sums-basic-recursion 0))
  (println "Problem 23 using Tail Recursion:" (non-abundant-sums-tail-recursion 0 0))
  (println "Problem 23 using Modular Realization:" (non-abundant-sums-modular 0))
  (println "Problem 23 using Map:" (non-abundant-sums-map 0))
  (println "Problem 23 using Loop:" (non-abundant-sums-loop 0))
  (println "Problem 23 using Lazy Collections:" (non-abundant-sums-lazy 0)))

