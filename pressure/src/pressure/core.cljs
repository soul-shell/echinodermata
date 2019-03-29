(ns pressure.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(enable-console-print!)

(defn spirograph [R r a x-scale y-scale t]
  [(* x-scale
     (+ (* (- R r) (q/cos (* (/ r R) t)))
        (* a (q/cos (* (- 1 (/ r R)) t)))))
   (* y-scale
     (+ (* (- R r) (q/sin (* (/ r R) t)))
        (* a (q/sin (* (- 1 (/ r R)) t)))))])

(def spirograph-functions
  [(partial spirograph 5 4 3 40 40)
   (partial spirograph 5 1 1 40 40)])

(def ^:const max-t (* 10 q/PI))

(defn setup []
  (q/frame-rate 60)
  (q/color-mode :hsb)
  ; initial state
  {:t 0
   :fn-index 0})

(defn update-state [state]
  (if (< (:t state) max-t)
    (update state :t + 0.2)
    (-> state
      (assoc :t 0)
      (update :fn-index (fn [i] (mod (inc i) (count spirograph-functions)))))))

(defn draw-plot [f from to step]
  (doseq [two-points (->> (range from to step)
                          (map f)
                          (partition 2 1))]
    (apply q/line two-points)))

(defn draw-state [state]
  (q/background 255)
    ; move origin point to the center of the sketch.
    (q/with-translation [(/ (q/width) 2)
                         (/ (q/height) 2)]
      (if (> (:fn-index state) 0)
        (let [prev-index (-> state :fn-index (- 1))
              prev-fn (nth spirograph-functions prev-index)]
          (println prev-fn)
          (draw-plot prev-fn (:t state) max-t 0.02)))
      (let [spiro-fn (nth spirograph-functions (:fn-index state))]
        (draw-plot spiro-fn 0 (:t state) 0.02))))

(defn ^:export run-sketch []
  (q/defsketch pressure
    :host "pressure"
    :size [600 600]
    :setup setup ; called once
    :update update-state ; called each frame
    :draw draw-state ; called after update
    :middleware [m/fun-mode]))
