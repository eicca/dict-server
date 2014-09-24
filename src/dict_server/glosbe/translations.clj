(ns dict-server.glosbe.translations
  (:require [net.cgrand.enlive-html :as html]
            [clj-http.client :as client]
            [dict-server.utils :refer :all]))

(def max-translations 5)

(defn translation-url
  [from dest phrase]
  (str "http://glosbe.com/" from "/" dest "/" phrase))

(defn fetch-url
  [url]
  (-> (client/get url {:throw-exceptions false})
      :body html/html-snippet))

; (client/get "http://glosbe.com/en/de/fasfjaslfkajsf" {:throw-exceptions false})

; (defn strip
;   [mixed-string]
;   (apply str (filter #(Character/isLetter %) mixed-string)))

; TODO could be a bit more smarter.
(defn clean
  "Cleans '(' ')' and double spaces."
  [dirty-string]
  (clojure.string/replace dirty-string #"[\(\)\n\r\t  ]" ""))

(defn parse-phrase
  [html-translation]
  (html/text (first (html/select html-translation [:div.text-info :> :strong]))))

(defn parse-sounds
  [html-translation]
  (map #(get-in % [:attrs :data-url-ogg])
       (html/select html-translation [:span.audioPlayer-container :> :span])))

(defn parse-lexical-defs
  [html-translation]
  (map clean (html/texts (html/select html-translation [html/root :> :i]))))

(defn parse-translation
  [html-translation]
  {:phrase (parse-phrase html-translation)
   :sounds (parse-sounds html-translation)
   :lexical (parse-lexical-defs html-translation)})

(defn assign-sources
  [translations from dest]
  (map (fn
         [translation]
         (conj translation {:source-name "glosbe"
                            :source-url (translation-url dest from (translation :phrase))}))
       translations))

(defn get-translations
  [from dest phrase]
  (let [source-url (translation-url from dest phrase)
        html-translations (html/select (fetch-url source-url)
                                       [:div#phraseTranslation :div :ul :li])
        translations (map parse-translation (take max-translations html-translations))]
    {:dest dest
     :translations (assign-sources translations from dest)
     :source-name "glosbe"
     :source-url source-url}))


; (def result1 (html/select (fetch-url (meta-translation-url "en" "de" "hello"))
;                                        [:div#phraseTranslation :> :div :> :ul :li]))

; (map #(get-in % [:attrs :data-url-ogg]) (html/select (first result1) [:span.audioPlayer-container :> :span]))

(get-translations "en" "de" "hello")

