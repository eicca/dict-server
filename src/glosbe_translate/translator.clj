(ns glosbe-translate.translator
  (:require [net.cgrand.enlive-html :as html]
            [clj-http.client :as client]
            [schema.core :as s]))

(def max-translations 5)

(defn- translation-url
  [source target query]
  (str "http://glosbe.com/" source "/" target "/" query))

(defn- fetch-url
  [url]
  (-> (client/get url {:throw-exceptions false})
      :body html/html-snippet))

(defn- clean
  "Cleans '(' ')' and double spaces."
  [in]
  (clojure.string/replace in #"[\(\)\n\r\t  ]" ""))

(defn- parse-translated-text
  [html-translation]
  (html/text (first (html/select html-translation [:div.text-info :> :strong]))))

(defn- parse-sounds
  [html-translation]
  (->> (html/select html-translation [:span.audioPlayer-container :> :span])
      (map #(get-in % [:attrs :data-url-ogg]))
      (map #(str "http://glosbe.com" %))))

(defn- parse-lexical-defs
  [html-translation]
  (map clean (html/texts (html/select html-translation [html/root :> :i]))))

(defn- parse-meaning
  [html-translation]
  {:translated-text (parse-translated-text html-translation)
   :sounds (parse-sounds html-translation)
   :lexical (parse-lexical-defs html-translation)})

(defn- assign-sources
  [translations source target]
  (map (fn
         [translation]
         (conj translation {:origin-name "glosbe"
                            :web-url (translation-url target source (translation :translated-text))}))
       translations))

(defn translate
  [source target query]
  (let [source-url (translation-url source target query)
        html-translations (html/select (fetch-url source-url)
                                       [:div#phraseTranslation :div :ul :li])
        meanings (map parse-meaning (take max-translations html-translations))]
    {:target target
     :meanings (assign-sources meanings source target)
     :web-url source-url}))

