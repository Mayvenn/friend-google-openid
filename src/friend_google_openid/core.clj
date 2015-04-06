(ns friend-google-openid.core
  (:require [cemerick.friend :as friend]
            [friend-oauth2.workflow :as oauth2]
            [friend-oauth2.util :refer [format-config-uri]]
            [cheshire.core :refer [parse-string]])
  (:import [com.google.api.client.googleapis.auth.oauth2 GoogleIdToken]
           [com.google.api.client.json.jackson2 JacksonFactory]))

(defn parse-jwt [{body :body}]
  (let [encrypted-id ((parse-string body) "id_token")
        token-payload (.getPayload (GoogleIdToken/parse (JacksonFactory/getDefaultInstance) encrypted-id))]
    (if (.getEmailVerified token-payload)
      {:email (.getEmail token-payload)}
      {})))

(defn uri-config
  [{:keys [client-secret client-id] :as config}]
  {:authentication-uri {:url "https://accounts.google.com/o/oauth2/auth"
                        :query {:client_id client-id
                                :response_type "code"
                                :redirect_uri (format-config-uri config)
                                :scope "email"}}

   :access-token-uri {:url "https://accounts.google.com/o/oauth2/token"
                      :query {:client_id client-id
                              :client_secret client-secret
                              :grant_type "authorization_code"
                              :redirect_uri (format-config-uri config)}}})

(defn authenticate [routes config]
  "Authenticates the routes with Google OpenID Connect.

  Config consists of :client-id, :client-secret, and these:
  :callback      - {:path "" :domain ""}
  :credential-fn - (fn [] )}"
  (friend/authenticate routes {:allow-anon? true
                               :workflows [(oauth2/workflow
                                            {:client-config config
                                             :uri-config (uri-config config)
                                             :credential-fn (config :credential-fn)
                                             :access-token-parsefn parse-jwt})]}))
