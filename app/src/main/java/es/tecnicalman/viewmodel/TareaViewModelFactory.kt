package es.tecnicalman.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.tecnicalman.utils.room.TareaDao

class TareaViewModelFactory(
    private val tareaDao: TareaDao,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TareaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TareaViewModel(tareaDao, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}