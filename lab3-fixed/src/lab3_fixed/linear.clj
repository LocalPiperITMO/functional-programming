(ns lab3-fixed.linear
  (:require [clojure.string :as str]))

(defn linear-interpolation [points step]
  (let [[p1 p2] points
        [x1 y1] p1
        [x2 y2] p2
        x-values (range x1 (+ step x2) step)]
    (map (fn [x]
           (let [t (/ (- x x1) (- x2 x1))]
             [(double x) (+ y1 (* t (- y2 y1)))]))
         x-values)))

(defn format-linear-output [x-values y-values]
  (let [x-str (str/join "\t" (map #(format "%.2f" %) x-values))
        y-str (str/join "\t" (map #(format "%.2f" %) y-values))]
    (str x-str "\n" y-str "\n")))