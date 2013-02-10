(ns jormungandr
  "A prototype-like object system based entirely on functions.")

(defn- base-object "An empty object that has & knows nothing."
  [attrs base-ops methods]
  (fn obj [op & args]
    (if-let [op* (base-ops op)]
      (apply op* attrs base-ops methods args)
      (if-let [meth* (methods op)]
        (apply meth* obj args)
        (throw (NoSuchMethodException. (str "The method " op " is not known.")))))))

;; [Basic object operations]
(defn- $get "Basic accessor."
  [attrs base-ops methods-table k]
  (attrs k))

(defn- $set "Basic mutator."
  [attrs base-ops methods-table k v]
  (base-object (assoc attrs k v) base-ops methods-table))

(defn- $unset "The equivalent to dissoc."
  [attrs base-ops methods-table k]
  (base-object (dissoc attrs k) base-ops methods-table))

(defn- $has? "For querying the attributes."
  [attrs base-ops methods-table k]
  (contains? attrs k))

(defn- $attrs "For querying the attributes."
  [attrs base-ops methods-table]
  (keys attrs))

(defn- $recall "Basic accessor."
  [attrs base-ops methods-table k]
  (methods-table k))

(defn- $learn "For learning new methods."
  [attrs base-ops methods-table method-name f]
  (base-object attrs base-ops (assoc methods-table method-name f)))

(defn- $forget "For unlearning old methods."
  [attrs base-ops methods-table method-name]
  (base-object attrs base-ops (dissoc methods-table method-name)))

(defn- $knows? "For querying knowledge of methods."
  [attrs base-ops methods-table method-name]
  (contains? methods-table method-name))

(defn- $knowledge "For querying knowledge of methods."
  [attrs base-ops methods-table method-name]
  (keys methods-table))

(defn- object
  "An object constructor with optional base arguments and methods to avoid using :$get and :$learn."
  [& [attrs methods]]
  (base-object (or attrs {})
               {:$get $get, :$set $set, :$unset $unset,
                :$has? $has?, :$attrs $attrs,
                :$recall $recall, :$learn $learn, :$forget $forget,
                :$knows? $knows?, :$knowledge $knowledge}
               (or methods {})))

;; [Macros]
(defmacro ->1
  "A \"threading macro\" alternative that puts the item at the start of each list.
Useful for threading call to an object."
  [item & exprs]
  (reduce (fn [head tail]
            (if (seq? tail)
              (cons head tail)
              (list head tail)))
          item
          exprs))
