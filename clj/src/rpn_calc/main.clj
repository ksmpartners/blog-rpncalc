(ns rpn-calc.main)

(def commands
     {
      '+ (fn [ { [x y & more] :stack } ]
           { :stack (cons (+ y x) more)})
      
      '- (fn [ { [x y & more] :stack } ]
           { :stack (cons (- y x) more)})

      '* (fn [ { [x y & more] :stack } ]
           { :stack (cons (* y x) more)})

      '/ (fn [ { [x y & more] :stack } ]
           { :stack (cons (/ y x) more)})

      'sto (fn [ { [rnum val & more] :stack regs :regs} ]
           { :stack more :regs (assoc regs rnum val)})

      'rcl (fn [ { [rnum & more] :stack regs :regs} ]
           { :stack (cons (regs rnum) more) })
      
      'drop (fn [ { [x & more] :stack } ]
              { :stack more})

      'quit (fn [ {  } ]
              false)
      })

(defn make-push-command [ object ] 
  (fn [ { stack :stack } ]
    { :stack (cons object stack) }))

(defn show-state [ { stack :stack } ]
  (doseq [[index val] (map list (range (count stack) 0 -1) (reverse stack) )]
    (printf "%d> %s\n" index val)))

(defn apply-command [ state command  ]
  (if-let [ state-update (command state)]
    (conj state state-update)
    false))

(defn find-command [ object ]
  (if (number? object)
    (make-push-command object)
    (commands object)))

(defn read-command [ str ]
  (find-command (read-string str)))

(defn make-composite-command [ subcommands ]
  (fn [ state ]
    (reduce apply-command state subcommands)))

(defn parse-command-string [ str ]
  (make-composite-command
   (map read-command (.split (.trim str) "\\s+"))))

(defn -main []
  (loop [ state { :stack () :regs (vec (take 20 (repeat 0))) } ]
    (show-state state)
    (print "> ")
    (flush)
    (let [command (parse-command-string (.readLine *in*))]
      (if-let [new-state (apply-command state command)]
        (recur new-state)
        nil))))

