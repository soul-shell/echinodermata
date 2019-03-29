(ns pressure.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(enable-console-print!)

(defn spirograph [R r a x-scale y-scale t]
  [(* x-scale
     (+ (* (- R r) (Math/cos (* (/ r R) t)))
        (* a (Math/cos (* (- 1 (/ r R)) t)))))
   (* y-scale
     (+ (* (- R r) (Math/sin (* (/ r R) t)))
        (* a (Math/sin (* (- 1 (/ r R)) t)))))])

(def spirograph-functions
  [(partial spirograph 5 4 3 40 40)
   (partial spirograph 5 1 1 40 40)])

(def ^:const max-t (* 10 Math/PI))
(def ^:const t-trail-len (* 5 Math/PI))
(def ^:const dt 0.02)
(def ^:const dt-steps-trail (/ t-trail-len dt))
(def ^:const dalpha (/ 255 dt-steps-trail))

(defn setup []
  (q/frame-rate 60)
  (q/color-mode :hsb 360 100 100)
  ; initial state
  {:t 0
   :fn-index 0})

(defn update-state [state]
  (if (< (:t state) max-t)
    (update state :t + 0.1)
    (-> state
      (assoc :t 0)
      (update :fn-index (fn [i] (mod (inc i) (count spirograph-functions)))))))

(defn point-pairs-with-step [f from to step]
  (->> (range from to step) (map f) (partition 2 1)))

(defn draw-state [state]
  ; reset the canvas
  (q/background 180 80 60)
  (q/stroke-weight 8)
  ; move the origin point to the center
  (q/with-translation [(/ (q/width) 2)
                       (/ (q/height) 2)]
    (let [t (:t state)
          from-t (max 0 (- t t-trail-len))
          spiro-fn (nth spirograph-functions (:fn-index state))
          point-pairs (point-pairs-with-step spiro-fn from-t t dt)
          dt-steps (/ (min t t-trail-len) dt)
          start-alpha (* dalpha (- dt-steps-trail dt-steps))]
      (doseq [[i pts] (map-indexed vector point-pairs)]
        (q/stroke 180 35 98 (+ start-alpha (* i dalpha)))
        (apply q/line pts)))))

(defn ^:export run-sketch []
  (q/defsketch pressure
    :host "pressure"
    :size [600 600]
    :setup setup ; called once
    :update update-state ; called each frame
    :draw draw-state ; called after update
    :middleware [m/fun-mode]))
