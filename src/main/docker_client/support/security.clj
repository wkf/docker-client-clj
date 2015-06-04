(ns docker-client.support.security
  (:import (org.bouncycastle.openssl PEMParser)
           (java.security KeyStore KeyStore$TrustedCertificateEntry KeyFactory)
           (java.security.cert Certificate CertificateFactory)
           (java.security.spec PKCS8EncodedKeySpec)
           (java.nio.file Files Paths OpenOption)
           (java.nio.charset Charset)))

(def keystore-pass "docker?")

(defn read-certs [& paths]
  (let [cf (CertificateFactory/getInstance "X.509")]
    (for [path paths]
      (->> (Files/newInputStream path (into-array OpenOption []))
           (.generateCertificate cf)))))

(defn read-keys [& paths]
  (let [kf (KeyFactory/getInstance "RSA")]
    (for [path paths]
      (->> (Files/newBufferedReader
             path (Charset/defaultCharset))
           PEMParser.
           .readObject
           .getPrivateKeyInfo
           .getEncoded
           PKCS8EncodedKeySpec.
           (.generatePrivate kf)))))

(defn options
  [path]
  (let [cert-path (Paths/get path (into-array String []))
        [ca-cert
         client-cert] (read-certs
                        (.resolve cert-path "ca.pem")
                        (.resolve cert-path "cert.pem"))
        [client-key] (read-keys
                       (.resolve cert-path "key.pem"))]
    {:keystore (doto (KeyStore/getInstance (KeyStore/getDefaultType))
                 (.load nil nil)
                 (.setCertificateEntry "client" client-cert)
                 (.setKeyEntry
                   "key" client-key (.toCharArray keystore-pass) (into-array Certificate [client-cert])))
     :keystore-pass keystore-pass
     :trust-store (doto (KeyStore/getInstance (KeyStore/getDefaultType))
                    (.load nil nil)
                    (.setEntry "ca" (KeyStore$TrustedCertificateEntry. ca-cert) nil))}))
