(defproject pressure "0.1.0-SNAPSHOT"
  :description "do you feel it yet?"
  :url "http://github.com/soul-shell"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [quil "2.8.0"]
                 [org.clojure/clojurescript "1.10.439"]]

  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-figwheel "0.5.15"]]
  :hooks [leiningen.cljsbuild]

  :clean-targets ^{:protect false} ["resources/public/js"]
  :cljsbuild
  {:builds [; development build with figwheel hot swap
            {:id "development"
             :source-paths ["src"]
             :figwheel true
             :compiler
             {:main "pressure.core"
              :output-to "resources/public/js/main.js"
              :output-dir "resources/public/js/development"
              :asset-path "js/development"}}
            ; minified and bundled build for deployment
            {:id "optimized"
             :source-paths ["src"]
             :compiler
             {:main "pressure.core"
              :output-to "resources/public/js/main.js"
              :output-dir "resources/public/js/optimized"
              :asset-path "js/optimized"
              :optimizations :advanced}}]})
