(ns ipbgp.test
  (:use [ipbgp])
  (:use [clojure.test]))

(deftest parse-origin-test
  (testing "Parse origin"
    (let [line "3356 | 4.0.0.0/9 | LEVEL3 | US | DSL-VERIZON.NET | GTE.NET LLC"
          res (parse-ans-line :origin line)]
      (is (= "US" (:cn res)))
      (is (= "3356" (:asn res)))
      (is (= "LEVEL3" (:asname res))))))

(deftest parse-peer-test
  (testing "Parse peer"
    (let [line "17.112.1.4 | 3356 7018 | 714 | 17.112.0.0/16 | APPLE-ENGINEERING | US | APPLE.COM | APPLE COMPUTER INC"
          res (parse-ans-line :peer line)]
      (is (= "US" (:cn res)))
      (is (= "714" (:asn res)))
      (is (= 2 (count (:peers res))))
      (is (= "APPLE-ENGINEERING" (:asname res))))))



