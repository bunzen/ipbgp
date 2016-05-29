(ns ipbgp
  (:use [clojure.string :only (trim join split)])
  (:import (java.net Socket) (java.io DataInputStream DataOutputStream)))

;; Copyright © 2016 Geir Skjotsift <geir@pogostick.net>
;;
;; Distributed under the 2-Clause BSD License
;;
;; This library wraps the Shadowserver IP-BGP bulk API as
;; described here:
;;
;; http://www.shadowserver.org/wiki/pmwiki.php/Services/IP-BGP
;;

(def host "asn.shadowserver.org")
(def port 43)

(defn- com [host port msg]
  (let [s (Socket. host port)
        in (-> (.getInputStream s)
               DataInputStream.)
        out (-> (.getOutputStream s)
                DataOutputStream.)]
    (do
      (.writeUTF out msg)
      (.flush out)
      (slurp in))))

(defn- wrap-ips
  "Wrap a list of ip-addresses in a string suitable for use with the
  Shadowserver IP-BGP bulk API.  The argument are a kind and a list of IPs'

  kind may be either :peer or :origin.
  ips is a list of IP addresses represented as strings."
  [kind ips]
  (str "begin "
       (if (= kind :peer)
         "peer\n"
         "origin\n")
       (join "\n" ips) "\nend\n"))

(defn- split-peers
  "The first element of the Shadowserver IP-BGP peers result is the query, the
  second is a space separeted list of peers. The argument res is a list of
  split IP-BGP elements."
  [res]
  (let [result (rest res)
        query (first res)
        peers (split (first result) #"\s+")]
    (cons peers (cons query (rest result)))))

(defn parse-ans-line
  "Parses a single line of the result from the Shadowserver IP-BGP bulk query
  API. The arguments are kind and a line of results.

  kind may be either :peer or :origin."
  [kind l]
  (let [res (split l #"\s*\|\s*")
        header '(:query :asn :prefix :asname :cn :domain :isp)]
    (case kind
      :origin (zipmap header res)
      :peer (->> res
                 (split-peers)
                 (zipmap (cons :peers header))))))

(defn- bulk-query
  [t a]
  (let [res (com host port (wrap-ips t a))
        lines (split res #"\n")]
    (map (partial parse-ans-line t) lines)))

(defmulti ip->asn
  "Dispatch function that allows for ip->asn to be called both with a single
  element as a string or a list of strings.

  t -> :peer, :origin
  a -> string or list of strings representing IP addresses."
  {:arglists '(t a)}
  (fn [t a] (seq? a)))

(defmethod ip->asn true
  [t a]
  (bulk-query t a))

(defmethod ip->asn false
  [t a]
  (bulk-query t `(~a)))

(defn asn->prefix
  "Lookup all prefixes associated to the ASN via the
  Shadowserver IP-BGP service."
  [asn]
  (let [res (com host port (str "prefix " asn "\n"))
        lines (split res #"\n")]
    lines))
