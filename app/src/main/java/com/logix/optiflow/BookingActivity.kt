package com.logix.optiflow

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.logix.optiflow.data.remote.toUserMessage
import com.logix.optiflow.di.SearchBookingModule
import com.logix.optiflow.domain.model.OpticalStore
import com.logix.optiflow.domain.model.TimeSlot
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

class BookingActivity : AppCompatActivity() {

    private val getOpticalStoresUseCase = SearchBookingModule.getOpticalStoresUseCase
    private val getStoreAvailabilityUseCase = SearchBookingModule.getStoreAvailabilityUseCase
    private val bookAppointmentUseCase = SearchBookingModule.bookAppointmentUseCase
    private val getPatientSessionUseCase = SearchBookingModule.getPatientSessionUseCase

    private var stores: List<OpticalStore> = emptyList()
    private var slots: List<TimeSlot> = emptyList()

    private val slotFormatter =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SearchBookingModule.init(applicationContext)
        setContentView(R.layout.activity_booking)

        val storeSpinner = findViewById<Spinner>(R.id.storeSpinner)
        val loadSlotsButton = findViewById<Button>(R.id.loadSlotsButton)
        val bookButton = findViewById<Button>(R.id.bookButton)
        val slotList = findViewById<ListView>(R.id.slotList)
        val statusText = findViewById<TextView>(R.id.bookingStatusText)
        val loadingIndicator = findViewById<ProgressBar>(R.id.bookingLoadingIndicator)

        lifecycleScope.launch {
            setLoading(true, loadingIndicator, loadSlotsButton, bookButton)
            try {
                val session = getPatientSessionUseCase()
                statusText.text =
                    if (session == null) {
                        getString(R.string.booking_login_required)
                    } else {
                        getString(R.string.booking_patient_ready, session.email, session.patientId)
                    }

                stores = getOpticalStoresUseCase()
                storeSpinner.adapter =
                    ArrayAdapter(
                        this@BookingActivity,
                        android.R.layout.simple_spinner_dropdown_item,
                        stores.map { it.name },
                    )
            } catch (error: Exception) {
                statusText.text = getString(R.string.booking_error, error.toUserMessage())
            } finally {
                setLoading(false, loadingIndicator, loadSlotsButton, bookButton)
            }
        }

        loadSlotsButton.setOnClickListener {
            val store = selectedStore(storeSpinner) ?: return@setOnClickListener
            lifecycleScope.launch {
                setLoading(true, loadingIndicator, loadSlotsButton, bookButton)
                try {
                    slots = getStoreAvailabilityUseCase(store.id)
                    renderSlots(slotList)
                    statusText.text = getString(R.string.booking_slots_loaded, slots.size, store.name)
                } catch (error: Exception) {
                    statusText.text = getString(R.string.booking_error, error.toUserMessage())
                } finally {
                    setLoading(false, loadingIndicator, loadSlotsButton, bookButton)
                }
            }
        }

        bookButton.setOnClickListener {
            val store = selectedStore(storeSpinner) ?: return@setOnClickListener
            val slot = selectedSlot(slotList) ?: run {
                statusText.text = getString(R.string.booking_select_slot)
                return@setOnClickListener
            }
            lifecycleScope.launch {
                setLoading(true, loadingIndicator, loadSlotsButton, bookButton)
                try {
                    val appointment = bookAppointmentUseCase(store.id, slot.id)
                    Log.d(TAG, "Booked appointment ${appointment.id} status=${appointment.status}")
                    statusText.text =
                        getString(
                            R.string.booking_confirmed,
                            appointment.id,
                            appointment.status,
                        )
                } catch (error: Exception) {
                    statusText.text = getString(R.string.booking_error, error.toUserMessage())
                } finally {
                    setLoading(false, loadingIndicator, loadSlotsButton, bookButton)
                }
            }
        }
    }

    private fun selectedStore(spinner: Spinner): OpticalStore? {
        if (stores.isEmpty()) return null
        return stores.getOrNull(spinner.selectedItemPosition)
    }

    private fun selectedSlot(listView: ListView): TimeSlot? {
        val position = listView.checkedItemPosition
        if (position == ListView.INVALID_POSITION) return null
        return slots.getOrNull(position)
    }

    private fun renderSlots(listView: ListView) {
        listView.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_list_item_single_choice,
                slots.map { slot ->
                    getString(
                        R.string.booking_slot_item,
                        slotFormatter.format(slot.startDateTime),
                        slotFormatter.format(slot.endDateTime),
                        slot.status,
                    )
                },
            )
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE
    }

    private fun setLoading(
        loading: Boolean,
        loadingIndicator: ProgressBar,
        loadSlotsButton: Button,
        bookButton: Button,
    ) {
        loadingIndicator.visibility = if (loading) View.VISIBLE else View.GONE
        loadSlotsButton.isEnabled = !loading
        bookButton.isEnabled = !loading
    }

    companion object {
        private const val TAG = "AppointmentBooking"
    }
}
