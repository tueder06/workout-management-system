package com.teodor.shared.service

import com.teodor.shared.domain.entities.Exercise
import com.teodor.shared.domain.ExerciseFilter
import com.teodor.shared.domain.ValueNotFoundException
import com.teodor.shared.domain.validators.ExerciseValidator
import com.teodor.shared.persistence.ExerciseRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.slf4j.LoggerFactory

class ExerciseServiceImpl(
    private val exerciseRepository: ExerciseRepository
) : ExerciseService {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val _updates = MutableSharedFlow<Unit>(replay = 0)
    override val updates = _updates.asSharedFlow()

    init {
        logger.debug("Initializing ExerciseServiceImpl")
    }

    override suspend fun findExerciseById(id: Long): Result<Exercise> {
        logger.debug("Trying to find exercise with id {}", id)
        return try {
            val foundExercise =  exerciseRepository.findById(id)
                ?: throw ValueNotFoundException("Exercise not found.")
            Result.success(foundExercise)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveCustomExercise(exercise: Exercise): Result<Unit> {
        logger.debug("Trying to save custom exercise: {}", exercise)
        return try {
            ExerciseValidator.validate(exercise)

            exerciseRepository.save(exercise)
            notifyUpdate()
            Result.success(Unit)
        } catch (e: Exception) {
            logger.error("Caught ", e)
            Result.failure(e)
        }
    }

    override suspend fun updateCustomExercise(newExercise: Exercise): Result<Unit> {
        logger.debug("Trying to update custom exercise with new values: {}", newExercise)
        return try {
            ExerciseValidator.validate(newExercise)

            exerciseRepository.update(newExercise)
                ?: throw ValueNotFoundException("Exercise not found.")
            notifyUpdate()
            Result.success(Unit)
        } catch (e: Exception) {
            logger.error("Caught ", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteCustomExercise(id: Long): Result<Exercise> {
        logger.debug("Trying to delete custom exercise with id {}", id)
        return try {
            val deletedExercise = exerciseRepository.delete(id)
                ?: throw ValueNotFoundException("Exercise not found.")
            notifyUpdate()
            Result.success(deletedExercise)
        } catch (e: Exception) {
            logger.error("Caught ", e)
            Result.failure(e)
        }
    }

    override suspend fun filterExercises(filter: ExerciseFilter): Result<Iterable<Exercise>> {
        logger.debug("Trying to search exercises using filter: {}", filter)
        return try {
            val exercises = exerciseRepository.filterExercises(filter)
            Result.success(exercises)
        } catch (e: Exception) {
            logger.error("Caught ", e)
            Result.failure(e)
        }
    }

    private suspend fun notifyUpdate() {
        _updates.emit(Unit)
    }
}