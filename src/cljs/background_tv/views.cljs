(ns background-tv.views
  (:require [re-frame.core :as re-frame]))

(defn main-panel []
  (let [yt-video-list (re-frame/subscribe [:yt-video-list])]
    (fn []
      [:div {:className "container"}

       [:div {:className "nav" 
              :style {:textAlign "center"}} 

        [:img {:src "http://i.imgur.com/fBH8TbM.png" :style {:width "90%" :margin "10px"}}]

        [:h2 {:style {:marginTop "5px"}} "Type some words..."]

        [:input {:type "text"
                 :placeholder "funny new crazy"
                 :style {:width "98%" :height "30px" :fontSize "1em" }
                 :on-change (fn [x] (re-frame/dispatch [:handler-with-http (-> x .-target .-value)]))}]

        [:div {:style {:margin "5px" :cursor "pointer"}}
         [:i {:style {:margin-right "5px"} 
              :className "fa fa-step-forward fa-2x"
              :onClick #(re-frame/dispatch [:next-video])}]]

        (when (not (empty? @yt-video-list))
          (for [video-data @yt-video-list]
            ^{:key (:video-id video-data)} 
            [:div {:style {:marginTop "10px"}} 
             [:img {:style {:width "100%"} :src (:thumbnail video-data)}]
             [:p (:title video-data)]]))]

       [:div {:className "article"}
        [:div {:id "player"}]]])))
