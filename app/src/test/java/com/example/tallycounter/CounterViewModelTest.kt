package com.example.tallycounter

import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [CounterViewModel].
 *
 * Uses MockK to mock the [CounterRepository] so tests run quickly and
 * deterministically without any Android framework dependencies.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CounterViewModelTest {

    private val countFlowValue = MutableStateFlow(0)
    private val savedValues = mutableListOf<Int>()
    private lateinit var repository: CounterRepository
    private lateinit var viewModel: CounterViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        Dispatchers.setMain(testDispatcher)

        repository = mockk()
        every { repository.countFlow } returns countFlowValue.asStateFlow()
        coEvery { repository.saveCount(any()) } answers {
            savedValues.add(args[0] as Int)
        }

        viewModel = CounterViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun countState_initiallyZero() {
        assertEquals(0, viewModel.countState().value)
    }

    @Test
    fun countState_restoresPersistedValue() {
        countFlowValue.value = 42
        assertEquals(42, viewModel.countState().value)
    }

    @Test
    fun increment_increasesCount() {
        viewModel.increment()
        assertEquals(1, viewModel.countState().value)

        viewModel.increment()
        assertEquals(2, viewModel.countState().value)
    }

    @Test
    fun decrement_decreasesCount() {
        countFlowValue.value = 5
        assertEquals(5, viewModel.countState().value)

        viewModel.decrement()
        assertEquals(4, viewModel.countState().value)
    }

    @Test
    fun decrement_doesNotGoBelowZero() {
        viewModel.decrement()
        assertEquals(0, viewModel.countState().value)

        viewModel.decrement()
        assertEquals(0, viewModel.countState().value)
    }

    @Test
    fun reset_setsCountToZero() {
        countFlowValue.value = 10
        assertEquals(10, viewModel.countState().value)

        viewModel.reset()
        assertEquals(0, viewModel.countState().value)
    }

    @Test
    fun increment_savesToRepository() {
        viewModel.increment()
        assertEquals(1, savedValues.last())
        coVerify { repository.saveCount(1) }
    }

    @Test
    fun decrement_savesToRepository() {
        countFlowValue.value = 3
        viewModel.decrement()
        assertEquals(2, savedValues.last())
        coVerify { repository.saveCount(2) }
    }

    @Test
    fun reset_savesToRepository() {
        countFlowValue.value = 7
        viewModel.reset()
        assertEquals(0, savedValues.last())
        coVerify { repository.saveCount(0) }
    }
}
