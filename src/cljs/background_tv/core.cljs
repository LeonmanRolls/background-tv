(ns background-tv.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [background-tv.events]
              [background-tv.subs]
              [background-tv.views :as views]
              [background-tv.config :as config]))

(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export ytready []
  (re-frame/dispatch [:initialize-youtube]))

(defn ^:export init []
  (re-frame/dispatch-sync [:initialize-db])
  (dev-setup)
  (mount-root))
