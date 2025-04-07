package com.playground.cardsignerplayground.nfc

import android.nfc.Tag
import android.util.Log
import es.gob.jmulticard.jse.provider.DnieProvider
import kotlinx.coroutines.delay
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.KeyStore
import java.security.KeyStore.PrivateKeyEntry
import java.security.Provider
import java.security.Security
import java.security.Signature

private const val KEYSTORE_TYPE_DNI = "DNI"
private const val CERT_ALIAS_AUTH: String = "CertAutenticacion"
private const val TAG: String = "NfcScanningManager"

class NfcScanningManager(
    private val can: String,
    private val pin: String,
) {

    init {
        addBouncyCastleManually()
    }

    private fun addBouncyCastleManually() {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(BouncyCastleProvider())
        }
    }

    suspend fun onTagRead(tag: Tag) {
        val provider = createProvider(tag)

        val builder: KeyStore.Builder = KeyStore.Builder.newInstance(
            KEYSTORE_TYPE_DNI,
            provider,
            KeyStore.CallbackHandlerProtection(DnieCallbackHandler(can, pin.toCharArray()))
        )

        val keyStore = builder.keyStore

        val keyEntry = keyStore.getEntry(CERT_ALIAS_AUTH, null) as? PrivateKeyEntry
            ?: throw RuntimeException("not handled")

        val signature = Signature.getInstance("SHA256withRSA", provider)
        signature.initSign(keyEntry.privateKey)
        signature.update("Hola mundo".toByteArray())

        val sign = signature.sign()

        Log.i(TAG, "onTagRead: OK all correct. Signed data: $sign")
    }

    private fun createProvider(tag: Tag): Provider {
        val provider = DnieProvider(AndroidNfcConnection(tag))
        if (Security.getProvider(provider.name) == null) {
            Security.insertProviderAt(provider, 1)
        }
        return provider
    }
}
