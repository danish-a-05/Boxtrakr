package com.example.boxtrakr.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.boxtrakr.data.BoxContentDao
import com.example.boxtrakr.data.BoxContentEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class BoxContentViewModel(private val dao: BoxContentDao) : ViewModel() {
    val boxContents: Flow<List<BoxContentEntity>> = dao.getAll()

    fun addBoxContent(name: String, quantity: Int, boxName: String) {
        viewModelScope.launch { dao.insert(BoxContentEntity(name = name, quantity = quantity, boxName = boxName)) }
    }

    companion object {
        fun factory(db: com.example.boxtrakr.data.AppDatabase) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return BoxContentViewModel(db.boxContentDao()) as T
            }
        }
    }
}
