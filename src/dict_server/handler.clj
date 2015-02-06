(ns dict-server.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.util.response :refer [response]]
            [ring.middleware.json :as middleware]
            [ring.middleware.cors :refer [wrap-cors]]
            [cheshire.core :refer :all]
            [dict-server.translator :as t]))

(defn- to-vector
  [param]
  (if (vector? param) param [param]))

(defroutes app-routes
  (context "/translations" []
           (GET "/" [from dest-locales phrase]
                (response (t/translate
                           {:from from
                            :dest-locales (to-vector dest-locales)
                            :phrase phrase}))))
                ; (response (parse-string (slurp "resources/local_resp.json"))))
  (GET "/suggestions" {params :params}
       (response (t/suggest params)))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
      (middleware/wrap-json-body)
      (middleware/wrap-json-response)
      (wrap-cors :access-control-allow-origin #".*"
                 :access-control-allow-methods [:get :put :post :delete :options])))
