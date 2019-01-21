package uk.whitecrescent.instrumentdigitizer

/**
 * Interface used for objects that will have a lifecycle, do not forget to call [destroy] when you are done with the
 * object.
 * You are not allowed to have initialization code in the Object's constructor, that is for [create], which must be
 * called explicitly
 */
interface Liveable {

    fun create(): Liveable

    fun destroy(): Liveable
}