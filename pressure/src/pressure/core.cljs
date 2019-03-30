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
  [(partial spirograph 5 4 1.2 40 40)
   (partial spirograph 5 1 1 40 40)
   (partial spirograph 5 1 2 40 40)])

(def ^:const max-t (* 10 Math/PI))
(def ^:const dt 0.02)
(def ^:const prev-fn-decay-rate 1.1)

(defn setup []
  (q/frame-rate 30)
  (q/color-mode :hsb 360 100 100)
  ; initial state
  {:t 0
   :fn-index 0
   :prev-fn-alpha 255
   :prev-fn-index -1})

(defn update-state [state]
  (if (< (:t state) max-t)
    (-> state
      (update :t + 1.2)
      (update :prev-fn-alpha (fn [a] (- a (max 1 (* 0.2 a))))))
    (-> state
      (assoc :t 0)
      (assoc :prev-fn-alpha 255)
      (update :fn-index (fn [i] (mod (inc i) (count spirograph-functions))))
      (update :prev-fn-index (fn [i] (mod (inc i) (count spirograph-functions)))))))

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
          spiro-fn (nth spirograph-functions (:fn-index state))
          point-pairs (point-pairs-with-step spiro-fn 0 t dt)]
      (q/stroke 180 35 98)
      (doseq [pts point-pairs]
        (apply q/line pts)))
    (if (not= -1 (state :prev-fn-index))
      (let [prev-fn (nth spirograph-functions (:prev-fn-index state))
            alpha (:prev-fn-alpha state)]
        (if (> alpha 0)
          ((q/stroke 180 35 98 alpha)
           (doseq [pts (point-pairs-with-step prev-fn 0 max-t dt)]
             (apply q/line pts))))))))

(defn ^:export run-sketch []
  (q/defsketch pressure
    :host "pressure"
    :size [600 600]
    :setup setup ; called once
    :update update-state ; called each frame
    :draw draw-state ; called after update
    :middleware [m/fun-mode]))
