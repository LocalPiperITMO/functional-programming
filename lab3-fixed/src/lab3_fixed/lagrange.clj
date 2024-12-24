(ns lab3-fixed.lagrange
  (:require [clojure.string :as str]))

(defn lagrange-polynomial [points x]
  (let [n (count points)]
    (reduce
     (fn [acc i]
       (let [[xi yi] (nth points i)
             li (reduce (fn [li j]
                          (if (not= i j)
                            (let [[xj _] (nth points j)]
                              (* li (/ (- x xj) (- xi xj))))
                            li))
                        1
                        (range n))]
         (+ acc (* li yi))))
     0
     (range n))))

(defn lagrange-interpolation [points step start-x end-x]
  (let [x-values (range start-x (+ end-x step) step)]
    (map (fn [x] [x (lagrange-polynomial points x)]) x-values)))

(defn format-lagrange-output [x-values y-values]
  (let [x-str (str/join "\t" (map #(format "%.2f" %) x-values))
        y-str (str/join "\t" (map #(format "%.2f" %) y-values))]
    (str x-str "\n" y-str "\n")))
