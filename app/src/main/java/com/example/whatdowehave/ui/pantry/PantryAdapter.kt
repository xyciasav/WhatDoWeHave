package com.example.whatdowehave.ui.pantry

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.whatdowehave.R

class PantryAdapter(
    private val items: MutableList<PantryItem>,
    private val listener: PantryItemListener
) : RecyclerView.Adapter<PantryAdapter.PantryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PantryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pantry, parent, false) // Ensure this matches the layout file
        return PantryViewHolder(view)
    }

    override fun onBindViewHolder(holder: PantryViewHolder, position: Int) {
        val item = items[position]
        holder.itemName.text = item.name
        holder.itemQuantity.text = "Qty: ${item.quantity}"

        // Show Low Stock Flag if quantity is below the threshold
        if (item.quantity < item.lowThreshold) {
            holder.lowStockFlag.visibility = View.VISIBLE
        } else {
            holder.lowStockFlag.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            listener.onEditItem(item, position)
        }

        holder.itemView.setOnLongClickListener {
            listener.onDeleteItem(item, position)
            true
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItem(position: Int, updatedItem: PantryItem) {
        items[position] = updatedItem
        notifyItemChanged(position)
    }

    fun deleteItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    class PantryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.tv_item_name)
        val itemQuantity: TextView = itemView.findViewById(R.id.tv_item_quantity)
        val lowStockFlag: TextView = itemView.findViewById(R.id.tv_low_stock_flag) // Ensure IDs match `item_pantry.xml`
    }

    interface PantryItemListener {
        fun onEditItem(item: PantryItem, position: Int)
        fun onDeleteItem(item: PantryItem, position: Int)
    }
}

