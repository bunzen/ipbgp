# ipbgp

A clojure library designed to work with the Shadowserver IP-BGP service API.

## Usage

Add [ipbgp "0.2.0"] to :dependencies in your lein project.clj

```clojure
=> (require 'ipbgp)
nil
user=> (ipbgp/ip->asn :origin ["44.55.66.77"])
({:query "44.55.66.77", :asn "7377", :prefix "44.0.0.0/8", :asname "UCSD", :cn "US", :isp "University of California, San Diego, US"})
user=> (ipbgp/ip->asn :origin '("44.55.66.77" "77.66.55.44"))
({:query "44.55.66.77", :asn "7377", :prefix "44.0.0.0/8", :asname "UCSD", :cn "US", :isp "University of California, San Diego, US"} {:query "77.66.55.44", :asn "16245", :prefix "77.66.0.0/17", :asname "NGDC,", :cn "DK", :isp "DK"})
user=>
=> (def pinfo (ipbgp/ip->asn :peer '("44.55.66.77" "77.66.55.44")))
=> (:peers (first pinfo))
["2152"]

=> (ipbgp/asn->prefix "7377")
["44.0.0.0/8" "69.166.11.0/24" "69.196.32.0/21" "69.196.40.0/21" "128.54.0.0/16" "132.239.0.0/16" "137.110.0.0/16" "169.228.0.0/16" "192.35.227.0/24" "192.35.228.0/24" "192.135.237.0/24" "192.135.238.0/24" "192.154.1.0/24" "198.134.135.0/24" "207.34.0.0/24" "216.21.14.0/24" "216.151.34.0/24" "216.151.38.0/24"]
=>
```

## License

Copyright © 2016,2017 Geir Skjotskift <geir@pogostick.net>

Distributed under the 2-Clause BSD License
