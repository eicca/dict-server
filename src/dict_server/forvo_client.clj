(ns dict-server.forvo-client
  (:require [clj-http.client :as client]
            [clojure.walk :refer [stringify-keys]]
            [clojure.string :refer [join]]
            [environ.core :refer [env]]))

(def api-key (env :forvo-key))

(def api-endpoint
  (str "http://apifree.forvo.com/key/" api-key
       "/format/json/action/standard-pronunciation/"))

(defn params-to-url
  [params]
  (str api-endpoint
       (join "/" (apply concat (stringify-keys params)))))

(defn get-sound
  [phrase locale]
  (let [url (params-to-url {:word phrase :language locale})
        resp (client/get url {:as :json})
        items ((resp :body) :items)]
    (if-let [item (first items)]
      (item :pathogg))))

