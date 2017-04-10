(ns background-tv.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 :yt-video-list
 (fn [db]
   (:yt-video-list db)))
