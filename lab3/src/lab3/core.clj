(ns lab3.core
  (:require [clojure.string :as str]
            [clojure.tools.cli :refer [parse-opts]]
            [lab3.linear :as linear]
            [lab3.lagrange :as lagrange])
  (:gen-class))

(def cli-options
  [["-s" "--step STEP" "Interpolation step" :parse-fn #(Double/parseDouble %)]
   ["-a" "--algorithm ALGORITHM" "Interpolation algorithm (linear, lagrange, both)"
    :validate [#{"linear" "lagrange" "both"} "Should be 'linear', 'lagrange' or 'both'"]]
   ["-h" "--help"]])

(defn parse-line [line]
  (let [[x y] (str/split line #"[;\t\s]+")]
    [(Double/parseDouble x) (Double/parseDouble y)]))

(defn process-input [step algorithm]
  (let [window-size 5]
    (loop [points []]
      (let [line (read-line)]
        (if (nil? line)
          (do
            (println "EOF received. Terminating...")
            (System/exit 0))
          (if (seq line)
            (let [point (parse-line line)
                  updated-points (conj points point)
                  sorted-points (if (not (apply <= (map first updated-points)))
                                  (do
                                    (println "Data not sorted by X. Sorting...")
                                    (sort-by first updated-points))
                                  updated-points)]
              (when (>= (count sorted-points) 2)
                (when (or (= algorithm "linear") (= algorithm "both"))
                  (let [window-points (take-last 2 sorted-points)
                        interp (linear/linear-interpolation window-points step)
                        x-values (map first interp)
                        y-values (map second interp)]
                    (println (format "Linear interpolation (X in range [%.3f, %.3f]):"
                                     (first x-values) (last x-values)))
                    (println (linear/format-linear-output x-values y-values)))))

              (when (or (= algorithm "lagrange") (= algorithm "both"))
                (let [lagrange-windows (partition window-size 1 sorted-points)]
                  (doseq [window lagrange-windows]
                    (let [start-x (first (map first window))
                          end-x (last (map first window))
                          interp (lagrange/lagrange-interpolation window step start-x end-x)
                          x-values (map first interp)
                          y-values (map second interp)]
                      (println (format "Lagrange interpolation (X in range [%.3f, %.3f]):"
                                       start-x end-x))
                      (println (lagrange/format-lagrange-output x-values y-values))))))

              (recur sorted-points))
            (recur points)))))))

(defn -main [& args]
  (let [{:keys [options]} (parse-opts args cli-options)]
    (if (:help options)
      (println "Usage: lein run --step <step> --algorithm <algorithm>")
      (let [step (or (:step options) 1.0)
            algorithm (or (:algorithm options) "both")]
        (println "Input points: X Y")
        (process-input step algorithm)))))