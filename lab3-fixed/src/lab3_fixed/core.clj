(ns lab3-fixed.core
  (:require [clojure.string :as str]
            [clojure.tools.cli :refer [parse-opts]]
            [lab3-fixed.linear :as linear]
            [lab3-fixed.lagrange :as lagrange])
  (:gen-class))

(def cli-options
  [["-s" "--step STEP" "Interpolation step" :parse-fn #(Double/parseDouble %)]
   ["-a" "--algorithm ALGORITHM" "Interpolation algorithm (linear, lagrange, both)"
    :validate [#{"linear" "lagrange" "both"} "Should be 'linear', 'lagrange' or 'both'"]]
   ["-w" "--window-size SIZE" "Window size for Lagrange interpolation" :parse-fn #(Integer/parseInt %)]
   ["-h" "--help"]])

(defn parse-line [line]
  (let [[x y] (str/split line #"[;\t\s]+")]
    [(Double/parseDouble x) (Double/parseDouble y)]))

(defn process-input [points step algorithm window-size]
  (when (< window-size 2)
    (println "Error: Window size must be at least 2 for Lagrange interpolation.")
    (System/exit 1))
  (let [sorted-points (sort-by first points)]
    ;; Linear interpolation
    (when (or (= algorithm "linear") (= algorithm "both"))
      (when (>= (count sorted-points) 2)
        (let [[p1 p2] (take-last 2 sorted-points)
              interp (linear/linear-interpolation [p1 p2] step)
              x-values (map first interp)
              y-values (map second interp)]
          (println (format "Linear interpolation (X in range [%.3f, %.3f]):"
                           (first x-values) (last x-values)))
          (println (linear/format-linear-output x-values y-values)))
        )
      )
    ;; Lagrange interpolation
    (when (or (= algorithm "lagrange") (= algorithm "both"))
        (when (>= (count sorted-points) window-size)
          (let [last-window (take-last window-size sorted-points)
                start-x (first (map first last-window))
                end-x (last (map first last-window))
                interp (lagrange/lagrange-interpolation last-window step start-x end-x)
                x-values (map first interp)
                y-values (map second interp)]
            (println (format "Lagrange interpolation (X in range [%.3f, %.3f]):"
                             start-x end-x))
            (println (lagrange/format-lagrange-output x-values y-values)))))))

(defn handle-input [step algorithm window-size]
  (loop [points []]
    (let [line (read-line)]
      (if (nil? line) ;; EOF or Ctrl+D
        (do
          (println "EOF received. Processing complete.")
          (System/exit 0))
        (if (seq line)
          (let [new-point (parse-line line)
                updated-points (conj points new-point)]
            ;; Process points dynamically
            (process-input updated-points step algorithm window-size)
            (recur updated-points))
          (recur points))))))

(defn -main [& args]
  (let [{:keys [options]} (parse-opts args cli-options)]
    (if (:help options)
      (println "Usage: lein run --step <step> --algorithm <algorithm> --window-size <size>")
      (let [step (or (:step options) 1.0)
            algorithm (or (:algorithm options) "both")
            window-size (or (:window-size options) 5)]
        (println "Input points: X Y (Press Ctrl+D to end input)")
        (if (.available System/in)
          (handle-input step algorithm window-size) ;; this is for interactive input
          (let [input-lines (line-seq (java.io.BufferedReader. *in*))]
            (if (empty? input-lines)
              (println "No input points provided. Exiting...")
              (let [points (map parse-line input-lines)]
                (process-input points step algorithm window-size)))))))))

