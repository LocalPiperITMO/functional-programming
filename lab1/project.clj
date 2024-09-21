(defproject lab1 "0.1.0"
  :description "Project Euler 8 and 23 problems solutions"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [clj-http "2.0.0"]]
  :main ^:skip-aot lab1.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
