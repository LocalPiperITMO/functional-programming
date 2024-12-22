(ns lab3.core
  (:gen-class)
  (:require [clojure.string :as str]))

(defn parse-input [line separator]
  (let [[x y] (str/split line (re-pattern separator))]
    [(Double/parseDouble x) (Double/parseDouble y)]))

(defn -main []
  (let [separator (System/getProperty "separator" ";")]
    (doseq [line (line-seq (java.io.BufferedReader. *in*))]
      (let [[x y] (parse-input line separator)]
        (println (format "%.3f %.3f" x y))))))


