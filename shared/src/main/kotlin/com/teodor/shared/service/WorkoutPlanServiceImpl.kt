package com.teodor.shared.service

import com.teodor.shared.domain.ValueNotFoundException
import com.teodor.shared.domain.entities.PlannedExercise
import com.teodor.shared.domain.entities.WorkoutDay
import com.teodor.shared.domain.entities.WorkoutPlan
import com.teodor.shared.domain.validators.PlannedExerciseValidator
import com.teodor.shared.domain.validators.WorkoutDayValidator
import com.teodor.shared.domain.validators.WorkoutPlanValidator
import com.teodor.shared.persistence.workout.PlannedExerciseRepository
import com.teodor.shared.persistence.workout.WorkoutDayRepository
import com.teodor.shared.persistence.workout.WorkoutPlanRepository
import org.slf4j.LoggerFactory

class WorkoutPlanServiceImpl(
    private val workoutPlanRepository: WorkoutPlanRepository,
    private val workoutDayRepository: WorkoutDayRepository,
    private val plannedExerciseRepository: PlannedExerciseRepository
) : WorkoutPlanService {

    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        logger.debug("Initializing WorkoutPlanServiceImpl")
    }

    override suspend fun getPlanDetails(id: Long): Result<WorkoutPlan> {
        logger.debug("Trying to find the workout plan having the id {}", id)

        return try {
            val workoutPlan = workoutPlanRepository.findById(id)
                ?: throw ValueNotFoundException("Workout plan not found.")

            Result.success(workoutPlan)
        } catch (e: Exception) {
            logger.error("Caught ", e)
            Result.failure(e)
        }
    }

    override suspend fun findWorkoutPlans(): Result<Iterable<WorkoutPlan>> {
        logger.debug("Trying to search workout plans for the logged in user")

        return try {
            val workoutPlans = workoutPlanRepository.findAllForUser()
            Result.success(workoutPlans)
        } catch (e: Exception) {
            logger.error("Caught ", e)
            Result.failure(e)
        }
    }

    override suspend fun saveWorkoutPlan(workoutPlan: WorkoutPlan): Result<Unit> {
        logger.debug("Trying to save workout plan: {}", workoutPlan)

        return try {
            WorkoutPlanValidator.validate(workoutPlan)

            workoutPlanRepository.save(workoutPlan)
            Result.success(Unit)
        } catch (e: Exception) {
            logger.error("Caught ", e)
            Result.failure(e)
        }
    }

    override suspend fun updateWorkoutPlan(newWorkoutPlan: WorkoutPlan): Result<Unit> {
        logger.debug("Trying to update the workout plan with new values: {}", newWorkoutPlan)

        return try {
            WorkoutPlanValidator.validate(newWorkoutPlan)

            workoutPlanRepository.update(newWorkoutPlan)
                ?: throw ValueNotFoundException("Workout plan not found.")

            Result.success(Unit)
        } catch (e: Exception) {
            logger.error("Caught ", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteWorkoutPlan(id: Long): Result<WorkoutPlan> {
        logger.debug("Trying to delete workout plan with id {}", id)

        return try {
            val deletedPlan = workoutPlanRepository.delete(id)
                ?: throw ValueNotFoundException("Workout plan not found.")

            Result.success(deletedPlan)
        } catch (e: Exception) {
            logger.error("Caught ", e)
            Result.failure(e)
        }
    }

    override suspend fun getDayDetails(id: Long): Result<WorkoutDay> {
        logger.debug("Trying to find the workout day having the id {}", id)

        return try {
            val workoutDay = workoutDayRepository.findById(id)
                ?: throw ValueNotFoundException("Workout day not found.")

            Result.success(workoutDay)
        } catch (e: Exception) {
            logger.error("Caught ", e)
            Result.failure(e)
        }
    }

    override suspend fun saveWorkoutDay(workoutDay: WorkoutDay): Result<Unit> {
        logger.debug("Trying to save new workout day: {}", workoutDay)

        return try {
            WorkoutDayValidator.validate(workoutDay)

            workoutDayRepository.save(workoutDay)
            Result.success(Unit)
        } catch (e: Exception) {
            logger.error("Caught ", e)
            Result.failure(e)
        }
    }

    override suspend fun updateWorkoutDay(newWorkoutDay: WorkoutDay): Result<Unit> {
        logger.debug("Trying to update workout day with new values: {}", newWorkoutDay)

        return try {
            WorkoutDayValidator.validate(newWorkoutDay)

            workoutDayRepository.update(newWorkoutDay)
                ?: throw ValueNotFoundException("Workout day not found.")

            Result.success(Unit)
        } catch (e: Exception) {
            logger.error("Caught ", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteWorkoutDay(id: Long): Result<WorkoutDay> {
        logger.debug("Trying to delete workout day having the id {}", id)

        return try {
            val deletedDay = workoutDayRepository.delete(id)
                ?: throw ValueNotFoundException("Workout day not found.")

            Result.success(deletedDay)
        } catch (e: Exception) {
            logger.error("Caught ", e)
            Result.failure(e)
        }
    }

    override suspend fun getExerciseDetails(id: Long): Result<PlannedExercise> {
        logger.debug("Trying to find the planned exercise having the id {}", id)

        return try {
            val plannedExercise = plannedExerciseRepository.findById(id)
                ?: throw ValueNotFoundException("Planned exercise not found.")

            Result.success(plannedExercise)
        } catch (e: Exception) {
            logger.error("Caught ", e)
            Result.failure(e)
        }
    }

    override suspend fun savePlannedExercise(plannedExercise: PlannedExercise): Result<Unit> {
        logger.debug("Trying to save new planned exercise for workout day: {}", plannedExercise)

        return try {
            PlannedExerciseValidator.validate(plannedExercise)

            plannedExerciseRepository.save(plannedExercise)
            Result.success(Unit)
        } catch (e: Exception) {
            logger.error("Caught ", e)
            Result.failure(e)
        }
    }

    override suspend fun updatePlannedExercise(newPlannedExercise: PlannedExercise): Result<Unit> {
       logger.debug("Trying to update planned exercise with new values: {}", newPlannedExercise)

        return try {
            PlannedExerciseValidator.validate(newPlannedExercise)

            plannedExerciseRepository.update(newPlannedExercise)
                ?: throw ValueNotFoundException("Planned exercise not found.")

            Result.success(Unit)
        } catch (e: Exception) {
            logger.error("Caught ", e)
            Result.failure(e)
        }
    }

    override suspend fun deletePlannedExercise(id: Long): Result<PlannedExercise> {
        logger.debug("Trying to delete planned exercise having the id {}", id)

        return try {
            val deleteExercise = plannedExerciseRepository.delete(id)
                ?: throw ValueNotFoundException("Planned exercise not found.")

            Result.success(deleteExercise)
        } catch (e: Exception) {
            logger.error("Caught ", e)
            Result.failure(e)
        }
    }
}