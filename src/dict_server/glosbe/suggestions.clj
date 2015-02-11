(ns dict-server.glosbe.suggestions
  (:require [clj-http.client :as client]
            [dict-server.utils :refer :all]
            [dict-server.google-translate :as gt]))

(def suggestions-endpoint "http://glosbe.com/ajax/phrasesAutosuggest")

(defn get-suggestions-for-pair
  [params]
  (let [resp (client/get suggestions-endpoint {:query-params params :as :json})]
    (resp :body)))

(defn get-all-suggestions
  [{:keys [locales fallback-locale phrase] :as params}]
  (when-let [detected-locale (gt/detect phrase locales fallback-locale)]
    {:locale detected-locale
     :suggestions (distinct
                    (flatten
                      (pmap #(get-suggestions-for-pair (conj % (find params :phrase)))
                            (pairs-for locales detected-locale))))}))

; (get-all-suggestions {:locales ["en" "de" "ru"] :fallback-locale "de" :phrase "irg"})
