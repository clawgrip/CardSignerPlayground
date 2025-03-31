package com.accenture.cardsignerplayground

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.accenture.cardsignerplayground.pages.home.HomePage
import com.accenture.cardsignerplayground.pages.home.HomeVMImpl
import com.accenture.cardsignerplayground.ui.theme.MyAppTheme

class MainActivity : ComponentActivity() {

    private val vm = HomeVMImpl()
    private val nfcAdapter: NfcAdapter? by lazy {
        NfcAdapter.getDefaultAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            MyAppTheme {
                HomePage(
                    vm = vm
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        stopListenNfc()
    }

    override fun onResume() {
        super.onResume()
        initListenNfc()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val tag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
        } else {
            intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        }

        if (tag == null) {
            Log.e(Companion.TAG, "tag is null")
            return
        }

        vm.onTagRead(tag)
    }

    private fun initListenNfc() {
        val nfcAdapter = nfcAdapter
        if (nfcAdapter == null) {
            Log.w(Companion.TAG, "initListenNfc: nfc is not supported on this device")
            return
        }

        // enableForegroundDispatch is mandatory call onResume()
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, javaClass)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0
        )
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    private fun stopListenNfc() {
        nfcAdapter?.disableForegroundDispatch(this)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}

