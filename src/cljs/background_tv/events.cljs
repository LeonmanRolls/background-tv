(ns background-tv.events
  (:require [re-frame.core :as re-frame]
            [background-tv.db :as db]
            [ajax.core :as ajax]
            [day8.re-frame.http-fx]
            [youtube-fx.core]))

(re-frame/reg-event-db
  :initialize-db
  (fn  [_ _]
    db/default-db))

(re-frame/reg-event-db
  :add-yt-id
  (fn [db [_ yt-id]]
    (update db :yt-id-list #(conj % yt-id))))

(re-frame/reg-event-db
  :delete-yt-id
  (fn [db [_ yt-id]]
    (update db :yt-id-list #(vec (remove (fn [x] (= yt-id x)) %)))))

(re-frame/reg-event-db
  :update-keywords
  (fn [db [_ keywords]]
    (let [words (clojure.string/split keywords #"\s")]
      (update db :keywords (fn [_] words)))))

(re-frame/reg-event-db
  :bad-http-result
  (fn [db [_ result]]
    (println "bad http result")
    db))

(defn rawyt->useableyt [yt-result]
  {:title (-> yt-result :snippet :title)
   :video-id (-> yt-result :id :videoId)
   :description (-> yt-result :snippet :description)
   :thumbnail (-> yt-result :snippet :thumbnails :high :url)})

(re-frame/reg-event-fx
  :process-youtube-search
  (fn [cfx [_ result]]
    (let [vids (vec (map rawyt->useableyt (:items result)))]
      {:db (assoc (:db cfx) :yt-video-list (rest vids))
       :youtube/load-video-by-id [:player (:video-id (first vids))]})))

(re-frame/reg-event-fx                       
  :handler-with-http   
  (fn [{:keys [db]} q]
    {:db   (assoc db :show-twirly true)  
     :http-xhrio {:method          :get
                  :params {:part "snippet"
                           :type "video"
                           :order "date"
                           :maxResults "20"
                           :q (last q) 
                           :key "AIzaSyCDtQT5et7aWtNhl7DSir_bOzPOP69Bbbs"}
                  :uri             "https://www.googleapis.com/youtube/v3/search"
                  :timeout         8000  
                  :format (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})   
                  :on-success      [:process-youtube-search]
                  :on-failure      [:bad-http-result]}}))

(re-frame/reg-event-fx
  :initialize-youtube
  (fn [_ _]
    {:youtube/initialize-player [:player
                                 {:height "100%"
                                  :width "100%"
                                  :events {:on-ready [:player-ready]
                                           :on-state-change [:player-state-change]}}]}))

(re-frame/reg-event-fx
  :next-video
  (fn [ctx _]   
    {:db (update (:db ctx) :yt-video-list (fn [x] (vec (rest x))))
     :youtube/load-video-by-id [:player (:video-id (first (-> (:db ctx) :yt-video-list )))]}))

(re-frame/reg-event-fx
  :player-state-change
  (fn [ctx [_ change-info]]   
    (let [{:keys [data]} (js->clj change-info :keywordize-keys true)]
      (when (= 0 data)
        {:db (update (:db ctx) :yt-video-list (fn [x] (vec (rest x))))
         :youtube/load-video-by-id [:player (:video-id (first (-> (:db ctx) :yt-video-list )))]}))))

(re-frame/reg-event-fx
  :new-video-playing
  (fn [_ [_ video-id]]   
    {:youtube/load-video-by-id [:player video-id]}))

(re-frame/reg-event-fx
  :new-video-queued
  (fn [_ [_ video-id start]]
    {:youtube/cue-video-by-id [:player {:video-id video-id   
                                                :start-seconds start}]}))

(re-frame/reg-event-fx
  :current-video-playing
  (fn [_ _]
    {:youtube/play-video :youtube-player}))

(re-frame/reg-event-fx
  :current-video-paused
  (fn [_ _]
    {:youtube/pause-video :youtube-player}))

(re-frame/reg-event-fx
  :player-ready
  (fn [_ _]
    {:youtube/load-video-by-id [:player "wpqATUfApkM"]}))

