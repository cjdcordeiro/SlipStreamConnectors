(ns slipstream.credcache.credential.myproxy-voms
  (:require
    [clojure.tools.logging :as log]
    [me.raynes.fs :as fs]
    [schema.core :as s]
    [slipstream.credcache.utils :as u]
    [slipstream.credcache.common :as c]
    [slipstream.credcache.credential :as cred]
    [slipstream.credcache.myproxy-utils :as myproxy]
    [slipstream.credcache.voms-utils :as voms]
    [slipstream.credcache.renewal :as r]))

(def ^:const credential-type "myproxy-voms")

(def ^:const resource-subtype-uri
  "http://sixsq.com/slipstream/credential/myproxy-voms")

;;
;; MyProxy/VOMS credential schema
;;

(def VomsAttributes
  {(s/either s/Keyword s/Str) {(s/optional-key :fqans)   [s/Str]
                               (s/optional-key :targets) [s/Str]}})

(def MyProxyVomsCommonAttributes
  {:subtypeURI                    (s/pred (fn [s] (= resource-subtype-uri s)))
   :myproxy-host                  s/Str
   (s/optional-key :myproxy-port) s/Int
   (s/optional-key :voms)         VomsAttributes
   (s/optional-key :email)        s/Str
   (s/optional-key :lifetime)     s/Int})

(def MyProxyVomsCredential
  (merge cred/Credential
         MyProxyVomsCommonAttributes
         {:proxy s/Str}))

(def MyProxyVomsCredentialTemplate
  (merge cred/CredentialTemplate
         MyProxyVomsCommonAttributes
         {:username   s/Str
          :passphrase s/Str}))

;;
;; utility method
;;

(defn proxy->tfile
  "Writes the proxy to a temporary file.  The temporary file is returned.
   The caller of this function is responsible for deleting the temporary
   file.  Returns null if the proxy could not be written to the file."
  [resource]
  (let [proxy (voms/base64->proxy (:proxy resource))
        tfile (fs/temp-file "proxy-")]
    (voms/proxy->file proxy tfile)
    tfile))

;;
;; method definitions
;;

(defmethod c/template->resource [cred/resource-template-type-uri resource-subtype-uri]
           [template]
  (let [lifetime (or (:lifetime template) myproxy/ttl-12h)
        bootstrap-cred (myproxy/get-proxy template)
        delegated-cred (myproxy/get-proxy {:proxy bootstrap-cred :lifetime lifetime})
        voms-info (:voms template)
        vproxy (voms/gsscred->vproxy delegated-cred voms-info)]
    (try
      (myproxy/destroy-proxy (assoc template :proxy bootstrap-cred))
      (catch Exception e
        (log/warn "unable to delete bootstrap credential:" (str e))))

    (-> template
        (voms/add-proxy-attributes vproxy)
        (c/update-resource-typeuri)
        (dissoc :username :passphrase))))

(defmethod c/validate-template [cred/resource-template-type-uri resource-subtype-uri]
           [template]
  (s/validate MyProxyVomsCredentialTemplate template)
  template)

(defmethod c/validate [cred/resource-type-uri resource-subtype-uri]
           [resource]
  (s/validate MyProxyVomsCredential resource)
  resource)

(defmethod r/renew [cred/resource-type-uri resource-subtype-uri]
           [resource]
  (try
    (let [gsscred (->> (:proxy resource)
                       (voms/base64->proxy)
                       (voms/x509cred->gsscred)
                       (assoc resource :proxy)
                       (myproxy/get-proxy))
          voms-info (:voms resource)
          vproxy (voms/gsscred->vproxy gsscred voms-info)]
      (voms/add-proxy-attributes resource vproxy))
    (catch Exception e
      (log/warn "exception raised when renewing myproxy-voms credentials:" (str e))
      nil)))
