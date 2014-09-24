(ns dict-server.translator
  (require [dict-server.glosbe.suggestions :as glosbe-suggestions]
           [dict-server.glosbe.translations :as glosbe-translations]
           [dict-server.google-translate :as google-translate]))

(defn fill-missing-with-google
  [meta-translation from dest phrase]
  (if (empty? (meta-translation :translations))
    (assoc meta-translation :translations [(google-translate/translate from dest phrase)])
    meta-translation))

; (fill-missing-with-google {} "en" "de" "hello")

(defn translate
  [{:keys [from dest-locales phrase]}]
  (let [meta-translations (pmap (fn [dest]
                             (-> (glosbe-translations/get-translations from dest phrase)
                                 (fill-missing-with-google from dest phrase)))
                           dest-locales)]
    {:phrase phrase :from from :meta-translations meta-translations}))

; (translate {:from "ru" :dest-locales ["de" "en"] :phrase "закуска"})

(defn suggest
  [params]
  (let [result (glosbe-suggestions/get-all-suggestions params)]
    (map (fn [suggestion] {:phrase suggestion :locale (result :locale)})
         (result :suggestions))))

; (suggest {:locales ["en" "de" "ru"] :fallback-locale "de" :phrase "irg"})

