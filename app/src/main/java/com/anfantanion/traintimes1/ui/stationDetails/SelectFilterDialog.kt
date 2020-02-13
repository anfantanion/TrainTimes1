package com.anfantanion.traintimes1.ui.stationDetails

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.anfantanion.traintimes1.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class SelectFilterDialog() : DialogFragment() {

    val stationDetailsViewModel = StationDetailsViewModel.shared!!
    lateinit var actualView: View

    var time = Calendar.getInstance()


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        if (activity == null) throw IllegalStateException("Activity cannot be null")

        val builder = AlertDialog.Builder(activity)
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_filter, null)
        actualView = view

        val dialog_filter_loc_linearlayout =
            view.findViewById<LinearLayout>(R.id.dialog_filter_loc_linearlayout)

        val dialog_filter_loc_checkbox =
            view.findViewById<CheckBox>(R.id.dialog_filter_loc_checkbox)
        val dialog_filter_date_checkbox =
            view.findViewById<CheckBox>(R.id.dialog_filter_date_checkbox)
        val dialog_filter_time_checkbox =
            view.findViewById<CheckBox>(R.id.dialog_filter_time_checkbox)

        val dialog_filter_date_textedit =
            view.findViewById<EditText>(R.id.dialog_filter_date_textedit)
        val dialog_filter_time_textedit =
            view.findViewById<EditText>(R.id.dialog_filter_time_textedit)

        updateTimeDateGUI()


        /**
         * Location
         */
        dialog_filter_loc_checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            when (isChecked) {
                true -> dialog_filter_loc_linearlayout.visibility = View.VISIBLE
                false -> dialog_filter_loc_linearlayout.visibility = View.GONE
            }
        }

        /**
         * Date
         */
        dialog_filter_date_checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            when (isChecked) {
                true -> {
                    dialog_filter_date_textedit.visibility = View.VISIBLE
                    //DatePickerFragment(this).show(parentFragmentManager, "datePicker")
                }
                false -> {
                    dialog_filter_date_textedit.visibility = View.INVISIBLE
                    resetDate()
                }
            }
        }
        dialog_filter_date_textedit.setOnClickListener {
            DatePickerFragment(this).show(parentFragmentManager, "datePicker")
        }

        /**
         * Time
         */
        dialog_filter_time_checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            when (isChecked) {
                true -> {
                    dialog_filter_time_textedit.visibility = View.VISIBLE
                    //TimePickerFragment(this).show(parentFragmentManager, "datePicker")
                }
                false -> {
                    dialog_filter_time_textedit.visibility = View.INVISIBLE
                    resetTime()
                }
            }
        }
        dialog_filter_time_textedit.setOnClickListener {
            TimePickerFragment(this).show(parentFragmentManager, "datePicker")
        }


        /**
         * Builder
         */
        builder
            .setTitle(R.string.stationDetails_FilterMessage)
            .setView(view)
            .setPositiveButton(R.string.stationDetails_setFilter) { dialog, id ->
                //Compile list of filters, compare, and force update if necessary
                val newFilters = HashMap<String,String>()
                if (dialog_filter_date_checkbox.isChecked || dialog_filter_time_checkbox.isChecked){
                    newFilters["date"] = dateToString(dialog_filter_time_checkbox.isChecked)
                }
                if (stationDetailsViewModel.filter != newFilters) {
                    stationDetailsViewModel.filter = newFilters
                    stationDetailsViewModel.getServices()
                }
            }
            .setNegativeButton(R.string.stationDetails_remFilter) { dialog, id ->
                if (stationDetailsViewModel.filter.isNotEmpty()) {
                    stationDetailsViewModel.filter = emptyMap()
                    stationDetailsViewModel.getServices()
                }

            }


        return builder.create()

    }

    /**
     * Time Requires a date before it, so always add it, even if today.
     */
    private fun dateToString(doTime: Boolean) : String{
        val sb = StringBuilder()
        sb.append("${time.get(Calendar.YEAR)}/${padInt(time.get(Calendar.MONTH)+1)}/${padInt(time.get(Calendar.DAY_OF_MONTH))}")
        if (doTime)
            sb.append("/${padInt(time.get(Calendar.HOUR_OF_DAY))}${padInt(time.get(Calendar.MINUTE))}")
        return sb.toString()
    }

    private fun padInt(int : Int) : String{
        return int.toString().padStart(2,'0')
    }

    private fun resetDate(){
        val now = Calendar.getInstance()
        time.set(Calendar.YEAR, now.get(Calendar.YEAR))
        time.set(Calendar.MONTH, now.get(Calendar.MONTH))
        time.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH))
    }

    private fun resetTime(){
        val now = Calendar.getInstance()
        time.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY))
        time.set(Calendar.MINUTE, now.get(Calendar.MINUTE))
    }

    fun updateTimeDateGUI() {
        val dialog_filter_date_textedit =
            actualView.findViewById<EditText>(R.id.dialog_filter_date_textedit)
        val dialog_filter_time_textedit =
            actualView.findViewById<EditText>(R.id.dialog_filter_time_textedit)
        var x = SimpleDateFormat.getDateInstance().format(time.time)

        dialog_filter_date_textedit.setText(
            SimpleDateFormat.getDateInstance().format(time.time),
            TextView.BufferType.NORMAL
        )
        dialog_filter_time_textedit.setText(
            SimpleDateFormat.getTimeInstance().format(time.time),
            TextView.BufferType.NORMAL
        )
    }

    class DatePickerFragment(private val selectFilterDialog: SelectFilterDialog) : DialogFragment(),
        DatePickerDialog.OnDateSetListener {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the current date as the default date in the picker

            // Create a new instance of DatePickerDialog and return it
            return DatePickerDialog(
                context!!,
                this,
                selectFilterDialog.time.get(Calendar.YEAR),
                selectFilterDialog.time.get(Calendar.MONTH),
                selectFilterDialog.time.get(Calendar.DAY_OF_MONTH)
            )
        }

        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            selectFilterDialog.time.set(Calendar.YEAR, year)
            selectFilterDialog.time.set(Calendar.MONTH, month)
            selectFilterDialog.time.set(Calendar.DAY_OF_MONTH, day)
            selectFilterDialog.updateTimeDateGUI()
        }


    }

    class TimePickerFragment(private val selectFilterDialog: SelectFilterDialog) : DialogFragment(),
        TimePickerDialog.OnTimeSetListener {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the current date as the default date in the picker

            // Create a new instance of DatePickerDialog and return it
            return TimePickerDialog(
                context!!,
                this,
                selectFilterDialog.time.get(Calendar.HOUR_OF_DAY),
                selectFilterDialog.time.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(context!!)
            )
        }

        override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
            selectFilterDialog.time.set(Calendar.HOUR_OF_DAY, hourOfDay)
            selectFilterDialog.time.set(Calendar.MINUTE, minute)
            selectFilterDialog.updateTimeDateGUI()
        }


    }
}