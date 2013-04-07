(defproject ch1 "0.1.0-SNAPSHOT"
  :description "CDAC: Chapter 1"
  :url "http://example.com/FIXME"
  :license {:name "WTFPL"
            :url "http://www.wtfpl.net/txt/copying/"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/data.json "0.2.1"]
                 [incanter/incanter-core "1.4.1"]
                 [incanter/incanter-charts "1.4.1"]
                 [incanter/incanter-excel "1.4.1"]
                 [incanter/incanter-io "1.4.1"]
                 [org.clojure/java.jdbc "0.2.3"]
                 [enlive "1.1.1"]
                 [org.xerial/sqlite-jdbc "3.7.2"]]
  :jvm-opts ["-Xmx1g"]
)
