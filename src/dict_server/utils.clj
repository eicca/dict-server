(ns dict-server.utils)

; TODO add more langs.
; NOTE locales should be listed in their "popularity" order.
(def availibale-locales ["en" "de" "ru"])

(defn locale-to-alpha3
  [locale]
  ({"en" "eng", "de" "deu", "ru" "rus"} locale))

(defn pairs-for
  [locales from-locale]
  (reduce (fn [pairs dest-locale]
            (if (= from-locale dest-locale)
              pairs
              (conj pairs {:from from-locale :dest dest-locale})))
          []
          locales))

(defn locale-pairs
  [locales]
  (reduce (fn [pairs locale]
            (concat pairs (pairs-for locales locale)))
          []
          locales))

