package com.logix.optiflow

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.logix.optiflow.di.SearchBookingModule
import com.logix.optiflow.domain.model.OpticalStore
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val getOpticalStoresUseCase = SearchBookingModule.getOpticalStoresUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loadingIndicator = findViewById<ProgressBar>(R.id.loadingIndicator)
        val statusText = findViewById<TextView>(R.id.statusText)
        val storeList = findViewById<ListView>(R.id.storeList)

        loadingIndicator.visibility = View.VISIBLE
        statusText.text = getString(R.string.optical_stores_loading)

        lifecycleScope.launch {
            try {
                val stores = getOpticalStoresUseCase()
                Log.d(TAG, "Loaded ${stores.size} optical stores: ${stores.map { it.name }}")
                renderStores(stores, statusText, storeList)
            } catch (error: Exception) {
                Log.e(TAG, "Failed to load optical stores", error)
                statusText.text =
                    getString(R.string.optical_stores_error, error.message ?: "unknown error")
                storeList.adapter = null
            } finally {
                loadingIndicator.visibility = View.GONE
            }
        }
    }

    private fun renderStores(
        stores: List<OpticalStore>,
        statusText: TextView,
        storeList: ListView,
    ) {
        if (stores.isEmpty()) {
            statusText.text = getString(R.string.optical_stores_empty)
            storeList.adapter = null
            return
        }

        statusText.text = getString(R.string.optical_stores_loaded, stores.size)
        storeList.adapter =
            object : ArrayAdapter<OpticalStore>(
                this,
                R.layout.item_optical_store,
                R.id.storeName,
                stores,
            ) {
                override fun getView(
                    position: Int,
                    convertView: View?,
                    parent: android.view.ViewGroup,
                ): View {
                    val view = super.getView(position, convertView, parent)
                    val store = getItem(position) ?: return view
                    view.findViewById<TextView>(R.id.storeName).text = store.name
                    view.findViewById<TextView>(R.id.storeAddress).text = store.address
                    val ratingText =
                        store.rating?.let { getString(R.string.optical_store_rating, it) } ?: ""
                    val phoneText = getString(R.string.optical_store_phone, store.phone)
                    view.findViewById<TextView>(R.id.storeMeta).text =
                        listOf(ratingText, phoneText).filter { it.isNotBlank() }.joinToString(" · ")
                    return view
                }
            }
    }

    companion object {
        private const val TAG = "OpticalStores"
    }
}
