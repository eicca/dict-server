(ns dict-server.glosbe-client
  (:require [clj-http.client :as client]
            [dict-server.utils :refer :all]
            [dict-server.google-translate :as gt]))

; Translation ===================

(def max-translations 3)

(def api-endpoint "http://glosbe.com/gapi/translate")

(defn first-meaning
  [locale tuc-meanings]
  (if-let [meaning (first (filter
                            #(= (% :language) (locale-to-alpha3 locale))
                            tuc-meanings))]
    (meaning :text)))

(defn source-url
  [from dest phrase]
  (str "http://glosbe.com/" dest "/" from "/" phrase))

(defn compose-translation
  [from dest tuc-obj]
  (if-let [tuc-phrase (tuc-obj :phrase)]
    (let [tuc-meanings (tuc-obj :meanings)]
      {:phrase (tuc-phrase :text)
       :meaning-from (first-meaning from tuc-meanings)
       :meaning-dest (first-meaning dest tuc-meanings)
       :source-url (source-url from dest (tuc-phrase :text))
       :source-name "glosbe"})))

(defn rebuild-results
  [from dest results]
  (reduce #(if-let [translation (compose-translation from dest %2)]
             (conj %1 translation)
             %1)
          []
          results))

(defn prepare-params
  [from dest phrase]
  {:from (locale-to-alpha3 from) :dest (locale-to-alpha3 dest) :phrase phrase})

(defn assoc-source
  [meta-translation from dest phrase]
  (if (empty? (meta-translation :translations))
    meta-translation ; Don't link phrases with empty translations.
    (conj meta-translation {:source-name "glosbe" :source-url (source-url from dest phrase)})))

(defn get-translations
  [from dest phrase]
  (let [prepared-params (conj (prepare-params from dest phrase) {:format "json"})
        resp (client/get api-endpoint {:query-params prepared-params :as :json})
        results (take max-translations ((resp :body) :tuc))
        rebuilded-results (rebuild-results from dest results)
        meta-translation {:dest dest :translations rebuilded-results}]
    (assoc-source meta-translation dest from phrase)))

; (get-translations "ru" "de" "кто-то")

;; Suggestions =======================

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

