package com.example.whatdowehave.ui.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatdowehave.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.Toast
import android.util.Log
import androidx.lifecycle.ViewModelProvider




class ShoppingListFragment : Fragment(), ShoppingListAdapter.ShoppingListListener {

    private lateinit var shoppingListAdapter: ShoppingListAdapter
    private lateinit var viewModel: ShoppingListViewModel

    private val shoppingListItems = mutableListOf<ShoppingListItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_shopping_list, container, false)

        // Set up RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_shopping_list_items)
        shoppingListAdapter = ShoppingListAdapter(mutableListOf(), this)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = shoppingListAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(requireActivity())[ShoppingListViewModel::class.java]

        // Observe changes in shopping list
        viewModel.shoppingListItems.observe(viewLifecycleOwner) { items ->
            shoppingListAdapter.updateItems(items)
        }

        // Set up Floating Action Button
        val fab = view.findViewById<FloatingActionButton>(R.id.fab_add_item)
        fab.setOnClickListener {
            showAddItemDialog()
        }
    }





    private fun showAddItemDialog() {
        // Inflate the dialog view
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_shopping_item, null)
        val itemNameInput = dialogView.findViewById<EditText>(R.id.et_item_name)
        val itemQuantityInput = dialogView.findViewById<EditText>(R.id.et_item_quantity)
        val addButton = dialogView.findViewById<Button>(R.id.btn_add_item)

        // Create the AlertDialog
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView) // Attach the dialog view
            .create()

        // Set OnClickListener for the Add button
        addButton.setOnClickListener {
            val name = itemNameInput.text.toString()
            val quantity = itemQuantityInput.text.toString().toIntOrNull() ?: 0

            if (name.isNotBlank() && quantity > 0) {
                val newItem = ShoppingListItem(name, quantity)
                viewModel.addItem(newItem) // Use ViewModel to add the item
                dialog.dismiss() // Close the dialog
            } else {
                if (name.isBlank()) itemNameInput.error = "Please enter a valid name"
                if (quantity <= 0) itemQuantityInput.error = "Please enter a valid quantity"
            }
        }

        dialog.show()
    }

    override fun onEditItem(item: ShoppingListItem, position: Int) {
        // Inflate the dialog view
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_shopping_item, null)
        val itemNameInput = dialogView.findViewById<EditText>(R.id.et_item_name)
        val itemQuantityInput = dialogView.findViewById<EditText>(R.id.et_item_quantity)
        val updateButton = dialogView.findViewById<Button>(R.id.btn_add_item)

        // Pre-fill the dialog with the current item's data
        itemNameInput.setText(item.name)
        itemQuantityInput.setText(item.quantity.toString())
        updateButton.text = "Update"

        // Create the AlertDialog
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // Set OnClickListener for the Update button
        updateButton.setOnClickListener {
            val updatedName = itemNameInput.text.toString()
            val updatedQuantity = itemQuantityInput.text.toString().toIntOrNull() ?: 0

            if (updatedName.isNotBlank() && updatedQuantity > 0) {
                val updatedItem = ShoppingListItem(updatedName, updatedQuantity)
                viewModel.updateItem(position, updatedItem) // Update the item in ViewModel
                dialog.dismiss() // Close the dialog
            } else {
                if (updatedName.isBlank()) itemNameInput.error = "Please enter a valid name"
                if (updatedQuantity <= 0) itemQuantityInput.error = "Please enter a valid quantity"
            }
        }

        dialog.show()
    }


    override fun onDeleteItem(item: ShoppingListItem, position: Int) {
        Log.d("ShoppingListFragment", "Delete button clicked for: ${item.name}")
        Toast.makeText(context, "Deleting: ${item.name}", Toast.LENGTH_SHORT).show()

        // Notify ViewModel to delete the item
        viewModel.deleteItem(position)
    }


}
