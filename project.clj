(defproject openrada.db "0.1.0-SNAPSHOT"
  :description "Openrada DB component"
  :url "https://github.com/openrada/db"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [camel-snake-kebab "0.2.5"]
                 [rethinkdb "0.3.29"]]
  :target-path "target/%s")
