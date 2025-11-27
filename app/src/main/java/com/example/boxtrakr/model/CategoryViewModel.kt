package com.example.boxtrakr.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.boxtrakr.data.CategoryDao
import com.example.boxtrakr.data.CategoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CategoryViewModel(private val dao: CategoryDao) : ViewModel() {
    val categories: Flow<List<CategoryEntity>> = dao.getAll()

    fun addCategory(name: String) {
        viewModelScope.launch { dao.insert(CategoryEntity(name = name)) }
    }

    companion object {
        fun factory(db: com.example.boxtrakr.data.AppDatabase) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CategoryViewModel(db.categoryDao()) as T
            }
        }
    }
}
