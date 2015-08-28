# friend-google-openid

This is a [workflow](https://github.com/cemerick/friend#workflows) for [Friend](https://github.com/cemerick/friend) that handles Google's [OpenID Connect](https://developers.google.com/identity/protocols/OpenIDConnect).

Bug reports and pull requests are most welcome.

## Rationale

We needed a way to enable users to use their Google Apps identities to authenticate. We implemented this as a reusable workflow that allows us to quickly add this capability to each of our different services.

Additionally, Google OpenID 2.0 is deprecated leaving no drop-in workflow.
(https://github.com/cemerick/friend/issues/117).

This workflow uses Google's [API Client Library for Java](https://developers.google.com/api-client-library/java/) to verify the JWT without the need to deal with explicitly fetching and caching Google's public certs. Auth is hard, so we believe Google's provided libraries will be more correct and reliable.

## Installation

friend-google-openid is available in Clojars. Add this `:dependency` to your Leiningen
`project.clj`:

```clojure
[friend-google-openid "0.2.1"]
```

friend-google-openid depends on friend-oauth2.

## Usage

This is just another Friend [workflow](https://github.com/cemerick/friend#workflows). Provide a `:credential-fn` along with `:client-id`, `:client-secret`, and a `:callback` hashmap.

You will need to create a project at [Google's Developer Console](https://console.developers.google.com/project/_/apiui/credential). 
Make sure the configured callback path and domain line up with what you provided in Developer Console.

```clojure
(ns your.ring.app
  (:require [friend-google-openid.core :as openid]))

(openid/workflow {:client-id client-id
                  :client-secret client-secret
                  :callback {:path "/oauth2callback"
                             :domain "http://your.domain.com"
                  :credential-fn credential-fn}})
```

## License

Copyright © 2015 [Mayvenn](http://mayvenn.com)

Distributed under the Eclipse Public License version 1.0.

