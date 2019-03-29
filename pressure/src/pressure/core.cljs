(ns pressure.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(defn setup []
  (q/frame-rate 60)
  (q/color-mode :hsb)
  ; initial state
  {:outer-hue 0 :outer-angle 0
   :inner-angle 0})

(defn update-state [state]
  {:outer-hue (mod (+ (:outer-hue state) 0.4) 255)
   :outer-angle (+ (:outer-angle state) 0.01)
   :inner-angle (+ (:inner-angle state) 0.04)})

(defn draw-state [state]
  (q/background 255)
  (let [x (* 150 (q/cos (:outer-angle state)))
        y (* 150 (q/sin (:outer-angle state)))
        inner-x (+ x (* 10 (q/cos (:inner-angle state))))
        inner-y (+ y (* 10 (q/sin (:inner-angle state))))]
    ; Move origin point to the center of the sketch.
    (q/with-translation [(/ (q/width) 2)
                         (/ (q/height) 2)]
      (q/fill (:outer-hue state) 255 255)
      (q/ellipse x y 100 100)
      (q/fill 0 0 0)
      (q/ellipse inner-x inner-y 10 10))))

(defn ^:export run-sketch []
  (q/defsketch pressure
    :host "pressure"
    :size [600 600]
    :setup setup ; called once
    :update update-state ; called each frame
    :draw draw-state ; called after update
    :middleware [m/fun-mode]))
