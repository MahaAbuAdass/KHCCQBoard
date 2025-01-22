package com.example.khccqboard.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.khccqboard.R
import com.example.khccqboard.data.CurrentQ


class TicketAdapter(
    private var currentQList: List<CurrentQ?>
) : RecyclerView.Adapter<TicketAdapter.ItemViewHolder>() {

    private val maxItems = 7

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ticketNumber: TextView = itemView.findViewById(R.id.cell_ticket_no)
        val counterNumber: TextView = itemView.findViewById(R.id.cell_counter_number)
        val doorNumber: TextView = itemView.findViewById(R.id.cell_door_number)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cell_ticket, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return maxItems // Return the maximum number of items
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        if (position < currentQList.size) {
            val item = currentQList[position]
            holder.counterNumber.text = item?.CounterId.toString()
            holder.ticketNumber.text = item?.TicketNo ?: " "
            holder.doorNumber.text = item?.CounterDoorNo
        } else {
            // If no data, set empty values or keep them as placeholders

            holder.ticketNumber.text = " \n -" // Keeps the layout even if empty

            holder.ticketNumber.textSize = 15f // Set text size in SP (e.g., 20f for 20sp)


        }
    }
}
