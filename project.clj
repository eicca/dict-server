(defproject glosbe-translate "0.1.0-SNAPSHOT"
  :description "json service for glosbe translations"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [ring/ring-json "0.4.0"]
                 [ring "1.4.0"]
                 [ring-server "0.4.0"]
                 [enlive "1.1.6"]
                 [compojure "1.4.0"]
                 [clj-http "2.0.0"]
                 [clj-http-fake "1.0.2"]
                 [cheshire "5.5.0"]
                 [prismatic/schema "1.0.4"]
                 [org.clojure/tools.nrepl "0.2.11"]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler glosbe-translate.handler/app}
  :profiles
  {:dev {:dependencies [[ring/ring-mock "0.3.0"]]}}
  :min-lein-version "2.0.0")
