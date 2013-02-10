# jormungandr

A prototype-like object system based on functions.

## Motivation

Nothing practical really.
I just figured that since people have already built functional programming on top of OO (e.g. Clojure), I might as well build OO on top of FP.

_Why a prototype-like OO system?_
I believe those are the only **true** object systems, since there are only objects and no classes.

That said, in `jormungandr` you don't do prototype inheritance by setting a "_proto_" attribute or something like that.
Being functional in nature, objects are immutable, so you inherit by simply modifying objects and working with the new copies.

## Usage

To get it, add this to your leiningen dependencies
`[jormungandr "0.1.0"]`

To use the library, add this to your code: `(use jormungandr)`.
Don't worry about the symbols being interned. There are only 2: `object`, a function; and `->1`, a macro.

This is `object`'s signature:  `(object [& [attrs methods]])`
You can create new objects by passing this function a map of attributes and a map of methods.
Method names should be keywords, but they can really be anything. The functions mapped to them must always take the object itself as their first argument.

You invoke methods like this: `(some-object :some-method arg1 arg2 ... argN)`

Objects come with various basic operations by default. You invoke them just like you would a method.
These are the operations and their signatures:

	(obj :$get k)                 ;; Basic getter.
	(obj :$set k v)               ;; Basic setter.
	(obj :$unset k)               ;; Basic unsetter (the equivalent to Clojure's dissoc).
	(obj :$has? k)                ;; The equivalent to Clojure's contains?.
	(obj :$attrs)                 ;; Gives you a list of attributes.
	(obj :$recall method)         ;; Given a method's name, returns the associated function.
	(obj :$learn method function) ;; Adds a new method.
	(obj :$forget method)         ;; Removes old method definitions.
	(obj :$knows? method)         ;; Like :$has?, but for methods.
	(obj :$knowledge)             ;; Like :$attrs, but for methods.

The `->1` macro is like the `->` macro we're all familiar with, except that it puts the items at the head of the list, and not at the 2nd position. That way you can chain method and operation calls easily.

## Examples

Simple stuff I came with just minutes ago...

	(def foo (object {:name "Mr. Foo", :age 20}))
	(foo :$get :name)
	
	(foo :throw-fancy-exception!) ;; Calling unknown method causes exceptions to be thrown.
		
	(->1 foo
	     (:$set :age 30)
	     (:$get :age))
	
	(->1 foo
	     (:$learn :->string (fn [self] (str (self :$get :name) " " (self :$get :age))))
	     :->string)
	
	(->1 foo
	     (:$learn :->map (fn [self]
	                       (let [attrs (self :$attrs)]
	                         (apply hash-map
	                                (interleave attrs
	                                            (map (partial self :$get)
	                                                 attrs))))))
	     :->map)


## License

Copyright © 2013 Eduardo Julián <eduardoejp@gmail.com>

Distributed under the Eclipse Public License, the same as Clojure.
