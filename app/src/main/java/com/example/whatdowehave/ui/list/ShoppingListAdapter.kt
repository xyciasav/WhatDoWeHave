package com.example.whatdowehave.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.whatdowehave.R
import android.widget.Button


class ShoppingListAdapter(
    private val items: MutableList<ShoppingListItem>,
    private val listener: ShoppingListListener
) : RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shopping_list, parent, false)
        return ShoppingListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        val item = items[position]
        holder.itemName.text = item.name
        holder.itemQuantity.text = "Qty: ${item.quantity}"

        // Handle edit on item click
        holder.itemView.setOnClickListener {
            listener.onEditItem(item, position) // Notify the listener for editing
        }

        // Handle delete on delete button click
        holder.deleteButton.setOnClickListener {
            listener.onDeleteItem(item, position) // Notify listener of delete
        }
    }



    override fun getItemCount(): Int = items.size

    fun addItem(item: ShoppingListItem) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun updateItems(newItems: List<ShoppingListItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }




    class ShoppingListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.tv_item_name)
        val itemQuantity: TextView = itemView.findViewById(R.id.tv_item_quantity)
        val deleteButton: Button = itemView.findViewById(R.id.btn_delete_item) // Add this line
    }


    interface ShoppingListListener {
        fun onEditItem(item: ShoppingListItem, position: Int)
        fun onDeleteItem(item: ShoppingListItem, position: Int)
    }

}
