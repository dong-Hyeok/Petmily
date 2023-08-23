package com.petmily.presentation.view.store

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.petmily.R
import com.petmily.databinding.ItemPointLogBinding
import com.petmily.presentation.view.MainActivity
import com.petmily.repository.dto.PointLog
import kotlinx.coroutines.NonDisposableHandle.parent
import java.text.SimpleDateFormat
import java.util.*

class PointLogAdapter(private val mainActivity: MainActivity) :
    RecyclerView.Adapter<PointLogAdapter.PointListViewHolder>() {

    private var pointLogList = mutableListOf<PointLog>()

    inner class PointListViewHolder(val binding: ItemPointLogBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindInfo(pointLog: PointLog) = with(binding) {
            tvPlayName.text = pointLog.pointContent

            tvPlayTime.text = dateParsing(pointLog.pointUsageDate)

            if (pointLog.pointType) {
                tvPoint.text = "+${pointLog.pointCost}"
                tvPlayName.setTextColor(ContextCompat.getColor(mainActivity, R.color.main_color))
                tvPoint.setTextColor(ContextCompat.getColor(mainActivity, R.color.main_color))
            } else {
                tvPoint.text = "-${pointLog.pointCost}"
                tvPlayName.setTextColor(ContextCompat.getColor(mainActivity, R.color.red))
                tvPoint.setTextColor(ContextCompat.getColor(mainActivity, R.color.red))
            }
        }
    }

    fun dateParsing(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy년 M월 d일", Locale.getDefault())

        val date: Date = inputFormat.parse(inputDate) ?: Date()
        return outputFormat.format(date)
    }

    fun setPointLogList(pointLogList: MutableList<PointLog>) {
        this.pointLogList = pointLogList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointListViewHolder {
        return PointListViewHolder(
            ItemPointLogBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        )
    }

    override fun onBindViewHolder(holder: PointListViewHolder, position: Int) {
        holder.bindInfo(pointLogList.get(position))
    }

    override fun getItemCount(): Int {
        return pointLogList.size
    }
}
