(ns glosbe-translate.data
  (:require [schema.core :as s]))

(def Locale s/Str)

(def TranslationReq
  {:source Locale
   :target Locale
   :query s/Str})

(def Meaning
  {(s/optional-key :lexical) [s/Str]
   :translated-text s/Str
   :sounds [s/Str]
   :origin-name s/Str
   :web-url s/Str})

(def Translation
  {:target Locale
   :web-url s/Str
   :meanings [Meaning]})
