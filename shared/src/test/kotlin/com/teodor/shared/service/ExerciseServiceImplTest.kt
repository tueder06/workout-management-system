package com.teodor.shared.service

import com.teodor.shared.domain.ExerciseFilter
import com.teodor.shared.domain.ValueNotFoundException
import com.teodor.shared.domain.entities.Exercise
import com.teodor.shared.domain.enums.EquipmentType
import com.teodor.shared.domain.enums.ExerciseType
import com.teodor.shared.domain.enums.MuscleGroup
import com.teodor.shared.persistence.ExerciseRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ExerciseServiceImplTest {
    private val exerciseRepository = mockk<ExerciseRepository>()
    private val exerciseService = ExerciseServiceImpl(exerciseRepository)
    private val validExercise = Exercise(
        id = 12,
        name = "Inclined Chest Press",
        mainMuscleGroup = MuscleGroup.CHEST,
        secondaryMuscleGroups = listOf(
            MuscleGroup.SHOULDERS,
            MuscleGroup.TRICEPS,
        ),
        equipmentUsed = listOf(
            EquipmentType.BENCH,
            EquipmentType.DUMBBELL,
        ),
        exerciseType = ExerciseType.REPETITION,
        userId = null,
    )

    private val invalidExercise = validExercise.copy(
        name = "  ",
        mainMuscleGroup = null,
        exerciseType = null,
    )

    @Test
    fun `saveCustomExercise succeeds`() = runTest {
        coEvery { exerciseRepository.save(any()) } returns validExercise
        val result = exerciseService.saveCustomExercise(validExercise)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `saveCustomExercise fails validation`() = runTest {
        val result = exerciseService.saveCustomExercise(invalidExercise)
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { exerciseRepository.save(any()) }
    }

    @Test
    fun `saveCustomExercise catches unexpected exception`() = runTest {
        coEvery { exerciseRepository.save(any()) } throws Exception("Crash")
        val result = exerciseService.saveCustomExercise(validExercise)
        assertTrue(result.isFailure)
    }

    @Test
    fun `updateCustomExercise succeeds`() = runTest {
        coEvery { exerciseRepository.update(any()) } returns validExercise
        val result = exerciseService.updateCustomExercise(validExercise)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `updateCustomExercise fails validation`() = runTest {
        val result = exerciseService.updateCustomExercise(invalidExercise)
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { exerciseRepository.update(any()) }
    }

    @Test
    fun `updateCustomExercise returns ValueNotFoundException if repo returns null`() = runTest {
        coEvery { exerciseRepository.update(any()) } returns null
        val result = exerciseService.updateCustomExercise(validExercise)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ValueNotFoundException)
    }

    @Test
    fun `updateCustomExercise catches unexpected exception`() = runTest {
        coEvery { exerciseRepository.update(any()) } throws Exception("Crash")
        val result = exerciseService.updateCustomExercise(validExercise)
        assertTrue(result.isFailure)
    }

    @Test
    fun `deleteCustomExercise succeeds`() = runTest {
        coEvery { exerciseRepository.delete(1L) } returns validExercise
        val result = exerciseService.deleteCustomExercise(1L)
        assertTrue(result.isSuccess)
        assertEquals(validExercise, result.getOrNull())
    }

    @Test
    fun `deleteCustomExercise fails with ValueNotFoundException if repo returns null`() = runTest {
        coEvery { exerciseRepository.delete(1L) } returns null
        val result = exerciseService.deleteCustomExercise(1L)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ValueNotFoundException)
    }

    @Test
    fun `deleteCustomExercise catches unexpected exception`() = runTest {
        coEvery { exerciseRepository.delete(any()) } throws Exception("Crash")
        val result = exerciseService.deleteCustomExercise(1L)
        assertTrue(result.isFailure)
    }

    @Test
    fun `filterExercises succeeds`() = runTest {
        val list = listOf(validExercise)
        coEvery { exerciseRepository.filterExercises(any()) } returns list
        val result = exerciseService.filterExercises(ExerciseFilter())
        assertTrue(result.isSuccess)
        assertEquals(list, result.getOrNull())
    }

    @Test
    fun `filterExercises catches unexpected exception`() = runTest {
        coEvery { exerciseRepository.filterExercises(any()) } throws Exception("Crash")
        val result = exerciseService.filterExercises(ExerciseFilter())
        assertTrue(result.isFailure)
    }

    @Test
    suspend fun `findExerciseById should return an exercise when found`() {
        coEvery { exerciseRepository.findById(1) } returns validExercise
        val result = exerciseService.findExerciseById(1)
        assertTrue(result.isSuccess)
        assertEquals(validExercise, result.getOrNull())
    }

    @Test
    suspend fun `findExerciseById should return failure when exercise is not found`() {
        coEvery { exerciseRepository.findById(999) } returns null
        val result = exerciseService.findExerciseById(999)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ValueNotFoundException)
    }

    @Test
    suspend fun `findExerciseById should return failure when an exception occurs`() {
        val exception = RuntimeException("Unexpected error")
        coEvery { exerciseRepository.findById(1) } throws exception
        val result = exerciseService.findExerciseById(1)
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}