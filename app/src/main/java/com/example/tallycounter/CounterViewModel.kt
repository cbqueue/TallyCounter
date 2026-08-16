package com.example.tallycounter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel holding the tally count in a [StateFlow].
 *
 * - Loads the persisted count from [CounterRepository] on creation.
 * - Saves every change back immediately so it survives app closures.
 * - Enforces a minimum value of 0.
 */
class CounterViewModel(private val repository: CounterRepository) : ViewModel() {

    private val count: MutableStateFlow<Int> = MutableStateFlow(0)

    init {
        // restore the persisted value and seed the UI state
        viewModelScope.launch {
            repository.countFlow.collect { saved ->
                count.value = saved
            }
        }
    }

    fun countState(): StateFlow<Int> = count.asStateFlow()

    fun increment() {
        update(count.value + 1)
    }

    fun decrement() {
        update((count.value - 1).coerceAtLeast(0))
    }

    fun reset() {
        update(0)
    }

    private fun update(value: Int) {
        count.update { value }
        viewModelScope.launch {
            repository.saveCount(value)
        }
    }
}
