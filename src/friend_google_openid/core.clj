(ns friend-google-openid.core
  (:require [cemerick.friend :as friend]
            [friend-oauth2.workflow :as oauth2]
            [friend-oauth2.util :refer [format-config-uri]]
            [cheshire.core :refer [parse-string]])
  (:import [com.google.api.client.googleapis.auth.oauth2 GoogleIdToken GoogleIdTokenVerifier]
           [com.google.api.client.json.jackson2 JacksonFactory]
           [com.google.api.client.http.apache ApacheHttpTransport]))

(defn parse-jwt [{body :body}]
  (let [encrypted-id ((parse-string body) "id_token")
        verifier (GoogleIdTokenVerifier. (ApacheHttpTransport.) (JacksonFactory/getDefaultInstance))
        token (.verify verifier encrypted-id)]
    (if (and token (-> token .getPayload .getEmailVerified))
      {:email (-> token .getPayload .getEmail)}
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

(defn workflow [config]
  "Authenticates the routes with Google OpenID Connect.

  Config consists of :client-id, :client-secret, and:
  :callback      - {:path \"\" :domain \"\"}
  :credential-fn - (fn [] )}"
  (oauth2/workflow
   {:client-config config
    :uri-config (uri-config config)
    :credential-fn (config :credential-fn)
    :access-token-parsefn parse-jwt}))
