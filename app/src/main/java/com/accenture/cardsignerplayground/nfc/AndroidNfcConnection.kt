package com.accenture.cardsignerplayground.nfc

import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.util.Log
import es.gob.jmulticard.HexUtils
import es.gob.jmulticard.apdu.ResponseApdu
import es.gob.jmulticard.apdu.dnie.VerifyApduCommand
import es.gob.jmulticard.connection.AbstractApduConnectionIso7816
import es.gob.jmulticard.connection.ApduConnection
import es.gob.jmulticard.connection.ApduConnectionException
import es.gob.jmulticard.connection.ApduConnectionProtocol
import java.io.IOException


class AndroidNfcConnection(tag: Tag?) : AbstractApduConnectionIso7816() {
    private val mIsoDep: IsoDep?

    /** Constructor de la clase para la gestin de la conexin por NFC.
     * @param tag `Tag` para obtener el objeto `IsoDep` y establecer la
     * conexin.
     * @throws IOException Si falla el establecimiento de la conexin.
     */
    init {
        requireNotNull(tag) {
            "El tag NFC no puede ser nulo" //$NON-NLS-1$
        }
        this.mIsoDep = IsoDep.get(tag)
        mIsoDep.connect()
        mIsoDep.timeout = ISODEP_TIMEOUT
    }

    @Throws(ApduConnectionException::class)
    public override fun internalTransmit(apdu: ByteArray): ResponseApdu {
        if (this.mIsoDep == null) {
            throw ApduConnectionException(
                "No se puede transmitir sobre una conexion NFC cerrada" //$NON-NLS-1$
            )
        }

        val isChv = apdu[1] == VerifyApduCommand.INS_VERIFY

        if (DEBUG) {
            Log.d(
                TAG, """
     Se va a enviar la APDU:
     ${if (isChv) "Verificacion de PIN" else HexUtils.hexify(apdu, apdu.size > 32)}
     """.trimIndent()
            ) //$NON-NLS-1$ //$NON-NLS-2$
        }

        val bResp: ByteArray
        try {
            bResp = mIsoDep.transceive(apdu)
        } catch (e: IOException) {
            // Evitamos que salga el PIN en la traza de excepcion
            throw ApduConnectionException(
                """
                    Error tratando de transmitir la APDU
                    ${if (isChv) "Verificacion de PIN" else HexUtils.hexify(apdu, apdu.size > 32)}
                    """.trimIndent(),  //$NON-NLS-1$ //$NON-NLS-2$
                e
            )
        }

        val response = ResponseApdu(bResp)

        if (DEBUG) {
            Log.d(
                TAG, """
     Respuesta:
     ${HexUtils.hexify(response.bytes, bResp.size > 32)}
     """.trimIndent()
            ) //$NON-NLS-1$
        }

        return response
    }

    @Throws(ApduConnectionException::class)
    override fun open() {
        try {
            if (!mIsoDep!!.isConnected) {
                mIsoDep.connect()
            }
        } catch (e: Exception) {
            throw ApduConnectionException(
                "Error intentando abrir la comunicacion NFC contra la tarjeta", e //$NON-NLS-1$
            )
        }
    }

    @Throws(ApduConnectionException::class)
    override fun close() {
        try {
            mIsoDep!!.close()
        } catch (ioe: IOException) {
            throw ApduConnectionException(
                "Error indefinido cerrando la conexion con la tarjeta", ioe //$NON-NLS-1$
            )
        }
    }

    @Throws(ApduConnectionException::class)
    override fun reset(): ByteArray {
        // No se cierran las conexiones por NFC
        if (this.mIsoDep != null) {
            if (mIsoDep.historicalBytes != null) {
                return mIsoDep.historicalBytes
            }
            return mIsoDep.hiLayerResponse
        }
        throw ApduConnectionException(
            "Error indefinido reiniciando la conexion con la tarjeta" //$NON-NLS-1$
        )
    }

    override fun getTerminals(onlyWithCardPresent: Boolean): LongArray {
        return longArrayOf(0)
    }

    override fun getTerminalInfo(terminal: Int): String {
        return "Interfaz ISO-DEP NFC de Android" //$NON-NLS-1$
    }

    override fun setTerminal(t: Int) {
        // Vacio, solo hay un terminal NFC por terminal
    }

    override fun isOpen(): Boolean {
        return mIsoDep!!.isConnected
    }

    override fun setProtocol(p: ApduConnectionProtocol) {
        // No hace nada, siempre es T=CL
    }

    override fun getSubConnection(): ApduConnection? {
        return null // Esta es la conexion de mas bajo nivel
    }

    override fun getMaxApduSize(): Int {
        return 0xff
    }

    companion object {
        private const val DEBUG = false
        private val TAG: String = AndroidNfcConnection::class.java.name

        private const val ISODEP_TIMEOUT = 3000

        /** *Version Code* de Android P.  */
        private const val ANDROID_P = 28
    }
}