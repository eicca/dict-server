(ns dict-server.utils)

(defn pairs-for
  "Returns an array of possible from-to pairs of locales.
  For example:
    (pairs-for [\"en\" \"de\" \"ru\"] \"en\")
  => [{:from \"en\", :dest \"de\"} {:from \"en\", :dest \"ru\"}]"
  [locales from-locale]
  (reduce (fn [pairs dest-locale]
            (if (= from-locale dest-locale)
              pairs
              (conj pairs {:from from-locale :dest dest-locale})))
          []
          locales))
