(ns glosbe-translate.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [glosbe-translate.data :as data]
            [glosbe-translate.translator :as t]
            [ring.middleware.json :as middleware]
            [ring.util.response :refer [response]]
            [schema.core :as s]))

(defn- extract-body
  [request]
  (->> request
      :body
      clojure.walk/keywordize-keys
      (s/validate data/TranslationReq)))

(defroutes app-routes
  (context "/translations" []
    (POST "/" request
      (let [body (extract-body request)
            {:keys [source target query]} body]
        (->> (t/translate source target query)
             (s/validate data/Translation)
             response))))
  (GET "/health" [] "All Systems Operational!")
  (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
      (middleware/wrap-json-body)
      (middleware/wrap-json-response)))

