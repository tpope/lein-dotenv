(ns lein-dotenv.plugin-test
  (:use clojure.test
        lein-dotenv.plugin))

(def fixture
  "# Comment
UNQUOTED=1 # Another comment
SINGLE='0\\n\\\\$UNQUOTED'
DOUBLE=\"0\\n\\\\$UNQUOTED\"
EXTERNAL=\"${PATH}\"
PATH='should not be overridden'
")

(deftest test-parsing
  (let [env (parse-str fixture)]
    (testing "parsing"
      (is (= "1" (env "UNQUOTED")))
      (is (= "0\\n\\\\$UNQUOTED" (env "SINGLE")))
      (is (= "0\n\\1" (env "DOUBLE")))
      (is (= (System/getenv "PATH") (env "EXTERNAL")))
      (is (nil? (env "PATH"))))))
