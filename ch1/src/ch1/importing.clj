; REPL: (require `ch1.importing :verbose :reload)
;       (ch1.importing/p10)
;       etc.

(ns ch1.importing
  (:require [incanter.core :refer [to-dataset dataset]]
            [incanter.io :refer [read-dataset]]
            [incanter.excel :refer [read-xls]]
            [net.cgrand.enlive-html :as html]
            [clojure.java.jdbc :refer [with-connection with-query-results]]
            [clojure.xml :refer [parse]]
            [clojure.string :as string]
            [clojure.zip :refer [children down right xml-zip]]
            [clojure.data.json :refer [read-str]]
            [cheshire.core :refer [parse-string]])
  (:import  [java.net URL]))

(defn p10
  "print results of read-dataset :header true"
  []
  (println "=== read-dataset ===")
  (print (read-dataset "data/small-sample.csv"))
  (println "=== read-dataset :header true ===")
  (print (read-dataset "data/small-sample.csv" :header true)))

(defn p12
  "print results of to-dataset json/read-str"
  []
  (println "=== to-dataset json/read-str ===")
  (print (to-dataset (read-str (slurp "data/small-sample.json")))))

(defn p12b
  "print results of to-dataset cheshire.core/parse-string"
  []
  (println "=== to-dataset cheshire.core/parse-string ===")
  (print (to-dataset (parse-string (slurp "data/small-sample.json")))))

(defn p13
  "print results of read-xls"
  []
  (println "=== read-xls ===")
  (print (read-xls "data/small-sample-header.xls")))

(def db {:subprotocol "sqlite"
         :subname "data/small-sample.sqlite"
         :classname "org.sqlite.JDBC"})

(defn load-table-data
  "This loads the data from a database table."
  [db table-name]
  (let [sql (str "SELECT * FROM "
                 table-name ";")]
    (with-connection db
      (with-query-results rs [sql]
        (to-dataset (doall rs))))))

(defn p15
  "print results of reading clojure.java.jdbc data"
  []
  (println "=== read via jdbc ===")
  (print (load-table-data db "people")))

(defn load-xml-data [xml-file first-data next-data]
  (let [data-map (fn [node]
                   [(:tag node) (first (:content node))])]
    (->>
      ;; 1. Parse the XML data file;
      (parse xml-file)
      xml-zip
      ;; 2. Walk it to extract the data nodes;
      first-data
      (iterate next-data)
      (take-while #(not (nil? %)))
      (map children)
      ;; 3. Convert them into a sequence of maps; and
      (map #(mapcat data-map %))
      (map #(apply array-map %))
      ;; 4. Finally convert that into an Incanter dataset
      to-dataset)))

(defn p17
  "print results of reading xml data"
  []
  (println "=== read via xml ===")
  (print (load-xml-data "data/small-sample.xml" down right)))

(defn to-keyword
  "This takes a string and returns a normalized keyword."
  [input]
  (-> input
    string/lower-case
    (string/replace \space \-)
    keyword))

(defn load-data
"This loads the data from a table at a URL."
  [url]
  (let [html (html/html-resource (URL. url))
        table (html/select html [:table#data])
        headers (->>
                  (html/select table [:tr :th])
                  (map html/text)
                  (map to-keyword)
                  vec)
        rows (->> (html/select table [:tr])
               (map #(html/select % [:td]))
               (map #(map html/text %))
               (filter seq))]
    (dataset headers rows)))

(defn p21
  "print results of web scaping with enlive"
  []
  (println "=== read via live web scrape ===")
  (print (load-data (str "http://www.ericrochester.com/"
                         "clj-data-analysis/data/small-sample-table.html"))))

(defn get-family
  "This takes an article element and returns the family name." 
  [article]
  (string/join (map html/text (html/select article [:header :h2]))))

(defn get-person
  "This takes a list item and returns a map of the persons' name and relationship."
  [li]
  (let [[{pnames :content} rel] (:content li)]
    {:name (apply str pnames)
     :relationship (string/trim rel)}))

(defn get-rows
  "This takes an article and returns the person mappings, with the family name added."
  [article]
  (let [family (get-family article)]
    (map #(assoc % :family family)
         (map get-person (html/select article [:ul :li])))))

(defn load-data "This downloads the HTML page and pulls the data out of it."
  [html-url]
  (let [html (html/html-resource (URL. html-url))
        articles (html/select html [:article])]
    (to-dataset (mapcat get-rows articles))))

(defn p24
  "print results of more advanced web scaping with enlive"
  []
  (println "=== read via live web scrape ===")
  (load-data (str "http://www.ericrochester.com/"
                  "clj-data-analysis/data/small-sample-list.html")))
