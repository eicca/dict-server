(ns dict-server.translator
  (require [dict-server.glosbe-client :as glosbe]
           [dict-server.forvo-client :as forvo]
           [dict-server.google-translate :as google-translate]))

(defn assoc-sound
  [item locale]
  (if-let [sound (forvo/get-sound (item :phrase) locale)]
    (assoc item :sound sound)
    item))

; (assoc-sound {:phrase "brother"} "en")

(defn assoc-from-sound
  [translation]
  (assoc-sound translation (translation :from)))

(defn assoc-dest-sounds
  [meta-translation]
  (let [locale (meta-translation :dest)
        translations (meta-translation :translations)
        translations-with-sounds (pmap #(assoc-sound % locale) translations)]
    (assoc meta-translation :translations translations-with-sounds)))

; (def m {:phrase "foo" :dest "en" :translations [{:phrase "brother"}{:phrase "mother234"}]})

; (assoc-dest-sounds m)

(defn fill-missing-with-google
  [meta-translation from dest phrase]
  (if (empty? (meta-translation :translations))
    (assoc meta-translation :translations [(google-translate/translate from dest phrase)])
    meta-translation))

; (fill-missing-with-google {} "en" "de" "hello")

(defn translate
  [{:keys [from dest-locales phrase]}]
  (let [meta-translations (pmap (fn [dest]
                             (-> (glosbe/get-translations from dest phrase)
                                 (assoc-dest-sounds)
                                 (fill-missing-with-google from dest phrase)))
                           dest-locales)]
    (assoc-from-sound {:phrase phrase :from from :meta-translations meta-translations})))

; (translate {:from "ru" :dest-locales ["de" "en" "ru"] :phrase "закуска"})

(defn suggest
  [params]
  (let [result (glosbe/get-all-suggestions params)]
    (map (fn [suggestion] {:phrase suggestion :locale (result :locale)})
         (result :suggestions))))

; (suggest {:locales ["en" "de" "ru"] :fallback-locale "de" :phrase "irg"})

