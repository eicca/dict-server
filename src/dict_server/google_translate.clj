(ns dict-server.google-translate
  (:require [clj-http.client :as client]
            [environ.core :refer [env]]
            [dict-server.utils :refer :all]))

(def api-key (env :google-key))

(def web-url "https://translate.google.com/#")

(def translate-endpoint "https://www.googleapis.com/language/translate/v2")

(def detect-endpoint
  (str translate-endpoint "/detect"))

; (action "detect")

(defn prepare-params
  [phrase]
  {:q phrase :key api-key})

(defn translate
  "Translates a phrase using Google Translate."
  [from dest phrase]
  (let [params (conj {:source from :target dest} (prepare-params phrase))
        resp (client/get translate-endpoint {:query-params params :as :json})]
    (when-let [result (first (((resp :body) :data) :translations))]
      {:source-name "google" :source-url (str web-url from "/" dest "/" phrase)
       :dest dest :phrase (result :translatedText)})))

; (translate "en" "de" "someone")

(defn detect
  "Detects a language by provided phrase.
  Accepts `locales` and `fallback-locale` as optional parameters:
  it will try to find locale in locales list with a fallback to `fallback-locale`."
  [phrase & [locales fallback-locale]]
  (let [resp (client/get detect-endpoint {:query-params (prepare-params phrase) :as :json})]
    (when-let [result (first (first (((resp :body) :data) :detections)))]
      (when-let [detected-lang (result :language)]
        (if locales
          (if (some #(= (result :language) %) locales)
            (result :language)
            fallback-locale)
          (result :language))))))

; (detect "irg" ["en" "de" "ru"] "de")
