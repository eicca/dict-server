(defproject dict-server "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring/ring-json "0.3.1"]
                 [ring-cors "0.1.4"]
                 [ring "1.3.1"]
                 [compojure "1.1.8"]
                 [clj-http "1.0.0"]
                 [cheshire "5.3.1"]
                 [clj-http-fake "0.7.8"]
                 [environ "1.0.0"]]
  :plugins [[lein-ring "0.8.11"]
            [lein-environ "1.0.0"]]
  :ring {:handler dict-server.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}}
  :min-lein-version "2.0.0")
