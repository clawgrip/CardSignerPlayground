package com.playground.cardsignerplayground

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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.playground.cardsignerplayground.pages.home.HomePage
import com.playground.cardsignerplayground.pages.home.HomeVMImpl
import com.playground.cardsignerplayground.ui.theme.MyAppTheme
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

private const val TAG = "MainActivity"

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

        lifecycleScope.launch {
            vm.shouldListenNFCTags
                .distinctUntilChanged()
                .onCompletion {
                    val isCancelled = (it is CancellationException)
                    Log.e(TAG, "onCreate: upstream completion. Is cancelled: $isCancelled")

                    stopListenNfc()
                }
                .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .onEach {
                    Log.d(TAG, "onCreate: updating NFC listening")
                    if (it) {
                        initListenNfc()
                    } else {
                        stopListenNfc()
                    }
                }
                .onCompletion {
                    val isCancelled = (it is CancellationException)
                    Log.d(TAG, "onCreate: on completion. Is cancelled: $isCancelled")

                    stopListenNfc()
                }
                .collect()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val tag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
        } else {
            intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        }

        if (tag == null) {
            Log.e(TAG, "tag is null")
            return
        }

        Log.d(TAG, "onNewIntent: Received nfc tag")

        vm.onTagRead(tag)
    }

    private fun initListenNfc() {
        Log.d(TAG, "initListenNfc: started listening")

        val nfcAdapter = nfcAdapter
        if (nfcAdapter == null) {
            Log.w(TAG, "initListenNfc: nfc is not supported on this device")
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
        Log.d(TAG, "stopListenNfc: Stopped listening")

        nfcAdapter?.disableForegroundDispatch(this)
    }
}

