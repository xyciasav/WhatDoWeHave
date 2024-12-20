package com.example.whatdowehave.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShoppingListViewModel : ViewModel() {
    private val _shoppingListItems = MutableLiveData<MutableList<ShoppingListItem>>(mutableListOf())
    val shoppingListItems: LiveData<MutableList<ShoppingListItem>> get() = _shoppingListItems

    fun addItem(item: ShoppingListItem) {
        val currentList = _shoppingListItems.value ?: mutableListOf()

        // Check if the item already exists
        val existingItem = currentList.find { it.name == item.name }
        if (existingItem != null) {
            existingItem.quantity += item.quantity // Update the quantity
        } else {
            currentList.add(item) // Add the new item
        }

        _shoppingListItems.value = currentList // Update LiveData
    }


    fun deleteItem(position: Int) {
        val currentList = _shoppingListItems.value ?: mutableListOf()
        if (position in currentList.indices) {
            currentList.removeAt(position) // Remove the item from the list
            _shoppingListItems.value = currentList // Update LiveData
        }
    }

    fun updateItem(position: Int, updatedItem: ShoppingListItem) {
        val currentList = _shoppingListItems.value ?: mutableListOf()
        if (position in currentList.indices) {
            currentList[position] = updatedItem // Update the item at the given position
            _shoppingListItems.value = currentList // Notify LiveData observers
        }
    }



}
