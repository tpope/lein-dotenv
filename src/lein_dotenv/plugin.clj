(ns lein-dotenv.plugin
  (:use [robert.hooke :only [add-hook]])
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [leiningen.core.eval :as eval]))

(defn- expand [v m]
  (str/replace v #"\$\{?(\w+)\}?|\\."
               (fn [[all k]]
                 (cond k (or (get m k (System/getenv k)) "")
                       (= all "\\n") "\n"
                       :else (subs all 1)))))

(def ^:private line-pattern #"(?mx)
  ^\s*(?:export\s+)?
  ([\w.]+)
  (?:\s*=\s*|:\s+)
  ((?:\\. | '[^']*' | \"(?:\\.|[^\"]*)\" | .)*?)
  \s*(?:\#.*)?$")

(defn parse-str [body]
  (reduce
    (fn [m [_ k v]]
      (if (m k (System/getenv k))
        m
        (assoc m k
               (str/replace
                 v
                 #"\"(?:\\.|[^\"])*\"|'[^']*?'|\$\w+|\$\{\w+\}"
                 #(case (first %)
                    \' (subs % 1 (dec (count %)))
                    \" (str/replace
                         (subs % 1 (dec (count %)))
                         #"\\.|\$\w+|\$\{\w+\}"
                         (fn [match] (expand match m)))
                    \\ (subs % 1)
                    \$ (expand % m))))))
    {}
    (re-seq line-pattern body)))

(defn parse-file [file]
  (when (.exists (io/file file))
    (parse-str (slurp file))))

(defn bind-env [func project form]
  (let [files (list* ".env" (:dotenv-files project))
        env (apply merge (map #(parse-file (io/file eval/*dir* %)) files))]
    (binding [eval/*env* (merge env eval/*env*)]
      (func project form))))

(defn- hooks []
  (add-hook #'eval/eval-in #'bind-env))
