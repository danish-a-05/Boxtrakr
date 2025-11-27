package com.example.boxtrakr.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.boxtrakr.data.BoxDao
import com.example.boxtrakr.data.BoxEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class BoxViewModel(private val dao: BoxDao) : ViewModel() {
    val boxes: Flow<List<BoxEntity>> = dao.getAll()

    fun addBox(name: String, categoryName: String) {
        viewModelScope.launch { dao.insert(BoxEntity(name = name, categoryName = categoryName)) }
    }

    companion object {
        fun factory(db: com.example.boxtrakr.data.AppDatabase) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return BoxViewModel(db.boxDao()) as T
            }
        }
    }
}
