package com.playground.cardsignerplayground.nfc

import android.util.Log
import es.gob.jmulticard.callback.CustomTextInputCallback
import java.lang.reflect.InvocationTargetException
import javax.security.auth.callback.Callback
import javax.security.auth.callback.CallbackHandler
import javax.security.auth.callback.PasswordCallback
import javax.security.auth.callback.UnsupportedCallbackException

private const val TAG = "DnieCallbackHandler"

class DnieCallbackHandler(
    private val can: String,
    private val pin: CharArray?,
) : CallbackHandler {

    @Throws(UnsupportedCallbackException::class)
    override fun handle(callbacks: Array<Callback?>?) {
        if (callbacks != null) {
            for (cb in callbacks) {
                if (cb != null) {
                    if ("javax.security.auth.callback.TextInputCallback" == cb.javaClass.name) {
                        try {
                            val m = cb.javaClass.getMethod(
                                "setText",
                                String::class.java
                            )
                            m.invoke(cb, can)
                        } catch (e: NoSuchMethodException) {
                            throw UnsupportedCallbackException(
                                cb,
                                "No se ha podido invocar al metodo 'setText' de la callback: $e"
                            )
                        } catch (e: SecurityException) {
                            throw UnsupportedCallbackException(
                                cb,
                                "No se ha podido invocar al metodo 'setText' de la callback: $e"
                            )
                        } catch (e: IllegalAccessException) {
                            throw UnsupportedCallbackException(
                                cb,
                                "No se ha podido invocar al metodo 'setText' de la callback: $e"
                            )
                        } catch (e: IllegalArgumentException) {
                            throw UnsupportedCallbackException(
                                cb,
                                "No se ha podido invocar al metodo 'setText' de la callback: $e"
                            )
                        } catch (e: InvocationTargetException) {
                            throw UnsupportedCallbackException(
                                cb,
                                "No se ha podido invocar al metodo 'setText' de la callback: $e"
                            )
                        }
                    } else if (cb is CustomTextInputCallback) {
                        cb.text = can
                    } else if (cb is PasswordCallback) {
                        cb.password = pin
                    } else {
                        throw UnsupportedCallbackException(cb)
                    }
                }
            }
        } else {
            Log.w(TAG, "Se ha recibido un array de Callbacks nulo")
        }
    }
}
