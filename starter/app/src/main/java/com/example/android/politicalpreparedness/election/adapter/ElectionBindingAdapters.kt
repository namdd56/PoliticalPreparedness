package com.example.android.politicalpreparedness

import android.icu.text.SimpleDateFormat
import android.widget.Button
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.network.models.Election
import java.util.Calendar
import java.util.Date
import java.util.Locale

@BindingAdapter("day")
fun setElectionDay(textView: TextView, date: Date?) {
    val calendar = Calendar.getInstance()
    date?.let {
        calendar.time = it
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        textView.text = dateFormat.format(calendar.time)
    }
}

@BindingAdapter("followOrUnfollow")
fun followOrUnfollow(button: Button, saved: Boolean?) {
    saved?.let {
        button.text =
            if (it) button.resources.getString(R.string.unfollow) else button.resources.getString(R.string.follow)
    }
}

@BindingAdapter("listData")
fun setElectionData(recyclerView: RecyclerView, data: List<Election>?) {
    val adapter = recyclerView.adapter as ElectionListAdapter
    adapter.submitList(data)
}
