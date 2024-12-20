package com.example.whatdowehave.ui.pantry

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatdowehave.R
import com.example.whatdowehave.ui.list.ShoppingListItem
import com.example.whatdowehave.ui.list.ShoppingListViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PantryFragment : Fragment(), PantryAdapter.PantryItemListener {

    private lateinit var pantryAdapter: PantryAdapter
    private val pantryItems = mutableListOf<PantryItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadPantryItems() // Load saved pantry items
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pantry, container, false)

        // Set up RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_pantry_items)
        pantryAdapter = PantryAdapter(pantryItems, this)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = pantryAdapter

        // Set up FAB to add new items
        val fab = view.findViewById<FloatingActionButton>(R.id.fab_add_item)
        fab.setOnClickListener {
            showAddItemDialog()
        }

        // Set up Move All Low button
        val moveLowButton = view.findViewById<Button>(R.id.btn_move_low_stock)
        moveLowButton.setOnClickListener {
            moveLowStockToShoppingList()
        }

        return view
    }

    private fun addItemToPantry(name: String, quantity: Int, lowThreshold: Int) {
        pantryItems.add(PantryItem(name, quantity, lowThreshold))
        pantryAdapter.notifyItemInserted(pantryItems.size - 1)
        savePantryItems()
    }

    private fun showAddItemDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_pantry_item, null)
        val itemNameInput = dialogView.findViewById<EditText>(R.id.et_item_name)
        val itemQuantityInput = dialogView.findViewById<EditText>(R.id.et_item_quantity)
        val itemThresholdInput = dialogView.findViewById<EditText>(R.id.et_item_threshold)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val addButton = dialogView.findViewById<Button>(R.id.btn_add_item)
        addButton.setOnClickListener {
            val name = itemNameInput.text.toString()
            val quantity = itemQuantityInput.text.toString().toIntOrNull() ?: 0
            val lowThreshold = itemThresholdInput.text.toString().toIntOrNull() ?: 1

            if (name.isNotBlank() && quantity > 0 && lowThreshold > 0) {
                addItemToPantry(name, quantity, lowThreshold)
                dialog.dismiss()
            } else {
                if (name.isBlank()) itemNameInput.error = "Enter a valid name"
                if (quantity <= 0) itemQuantityInput.error = "Enter a valid quantity"
                if (lowThreshold <= 0) itemThresholdInput.error = "Enter a valid threshold"
            }
        }

        dialog.show()
    }

    private fun moveLowStockToShoppingList() {
        val lowStockItems = pantryItems.filter { it.quantity < it.lowThreshold }
        val movedItems = mutableListOf<ShoppingListItem>()

        // Retrieve current shopping list from ViewModel
        val shoppingListViewModel = ViewModelProvider(requireActivity())[ShoppingListViewModel::class.java]
        val currentShoppingList = shoppingListViewModel.shoppingListItems.value ?: mutableListOf()

        for (item in lowStockItems) {
            val existingItem = currentShoppingList.find { it.name == item.name }
            if (existingItem == null) {
                val shoppingItem = ShoppingListItem(item.name, item.lowThreshold - item.quantity)
                movedItems.add(shoppingItem)
            }
        }

        for (movedItem in movedItems) {
            shoppingListViewModel.addItem(movedItem)
        }

        if (movedItems.isNotEmpty()) {
            Toast.makeText(context, "Moved ${movedItems.size} low-stock items to the shopping list", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "No new low-stock items to move", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onEditItem(item: PantryItem, position: Int) {
        showEditItemDialog(item, position)
    }

    override fun onDeleteItem(item: PantryItem, position: Int) {
        pantryAdapter.deleteItem(position)
        savePantryItems()
    }

    private fun showEditItemDialog(item: PantryItem, position: Int) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_pantry_item, null)
        val itemNameInput = dialogView.findViewById<EditText>(R.id.et_item_name)
        val itemQuantityInput = dialogView.findViewById<EditText>(R.id.et_item_quantity)
        val itemThresholdInput = dialogView.findViewById<EditText>(R.id.et_item_threshold)

        itemNameInput.setText(item.name)
        itemQuantityInput.setText(item.quantity.toString())
        itemThresholdInput.setText(item.lowThreshold.toString())

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val addButton = dialogView.findViewById<Button>(R.id.btn_add_item)
        addButton.text = "Update"
        addButton.setOnClickListener {
            val updatedName = itemNameInput.text.toString()
            val updatedQuantity = itemQuantityInput.text.toString().toIntOrNull() ?: 0
            val updatedThreshold = itemThresholdInput.text.toString().toIntOrNull() ?: 1

            if (updatedName.isNotBlank() && updatedQuantity > 0 && updatedThreshold > 0) {
                val updatedItem = PantryItem(updatedName, updatedQuantity, updatedThreshold)
                pantryAdapter.updateItem(position, updatedItem)
                savePantryItems()
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun savePantryItems() {
        val sharedPreferences = requireContext().getSharedPreferences("pantry_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val pantryJson = Gson().toJson(pantryItems)
        editor.putString("pantry_items", pantryJson)
        editor.apply()
    }

    private fun loadPantryItems() {
        val sharedPreferences = requireContext().getSharedPreferences("pantry_prefs", Context.MODE_PRIVATE)
        val pantryJson = sharedPreferences.getString("pantry_items", null)

        if (pantryJson != null) {
            val itemType = object : TypeToken<MutableList<PantryItem>>() {}.type
            val loadedItems: MutableList<PantryItem> = Gson().fromJson(pantryJson, itemType)
            pantryItems.clear()
            pantryItems.addAll(loadedItems)
        }

        if (pantryItems.isEmpty()) {
            pantryItems.addAll(
                listOf(
                    PantryItem("Cereal", 2, lowThreshold = 2),
                    PantryItem("Milk", 1, lowThreshold = 2),
                    PantryItem("Eggs", 12, lowThreshold = 6)
                )
            )
            savePantryItems()
        }
    }
}
