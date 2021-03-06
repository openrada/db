(defproject openrada/db "0.1.0-SNAPSHOT"
  :description "Openrada DB component"
  :url "https://github.com/openrada/db"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [rethinkdb "0.3.31"]
                 [com.stuartsierra/component "0.2.2"]]
  :target-path "target/%s")
