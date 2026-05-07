package com.teodor.shared.persistence.workout

import com.teodor.shared.domain.entities.WorkoutPlan
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import org.slf4j.LoggerFactory

class WorkoutPlanDbRepository(
    private val supabase: SupabaseClient
) : WorkoutPlanRepository {

    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        logger.debug("Initializing WorkoutPlanDbRepository")
    }

    private val table = supabase.from("workout_plans")

    override suspend fun save(entity: WorkoutPlan): WorkoutPlan {
        logger.debug("Saving workout plan: {}", entity)
        val userId = supabase.auth.currentUserOrNull()?.id
        logger.debug("Currently logged user is {}", userId)
        val planToSave = entity.copy(userId = userId)
        val savedWorkoutPlan = table.insert(planToSave) {
            select()
        }.decodeSingle<WorkoutPlan>()
        logger.debug("Workout plan stored successfully: {}", savedWorkoutPlan)
        return savedWorkoutPlan
    }

    override suspend fun findById(id: Long): WorkoutPlan? {
        logger.debug("Finding workout plan with id {}", id)
        val foundWorkoutPlan = table.select(
            columns = Columns.raw("*, workout_days(*, planned_exercises(*))")
        ) {
            filter { eq("id", id) }
        }.decodeSingleOrNull<WorkoutPlan>()

        logger.debug("Workout plan found: {}", foundWorkoutPlan?.name ?: "NOT FOUND")
        logger.debug("Loaded {} days for this plan.", foundWorkoutPlan?.days?.size ?: 0)
        return foundWorkoutPlan
    }

    override suspend fun update(entity: WorkoutPlan): WorkoutPlan? {
        logger.debug("Updating workout plan with new values: {}", entity)
        val workoutPlanId = entity.id ?: return null
        val userId = supabase.auth.currentUserOrNull()?.id
        logger.debug("Currently logged user updating is {}", userId)
        val planToUpdate = entity.copy(userId = userId)

        val updatedWorkoutPlan = table.update(planToUpdate) {
            filter { eq("id", workoutPlanId) }
            select()
        }.decodeSingleOrNull<WorkoutPlan>()
        logger.debug("The updated workout plan is: {}", updatedWorkoutPlan)
        return updatedWorkoutPlan
    }

    override suspend fun delete(id: Long): WorkoutPlan? {
        logger.debug("Deleting workout plan with id {}", id)
        val deletedWorkoutPlan = table.delete {
            filter { eq("id", id) }
            select()
        }.decodeSingleOrNull<WorkoutPlan>()
        logger.debug("Deleted workout plan: {}", deletedWorkoutPlan)
        return deletedWorkoutPlan
    }

    override suspend fun findAllForUser(): Iterable<WorkoutPlan> {
        logger.debug("Finding all workout plan for the logged user")
        val userId = supabase.auth.currentUserOrNull()?.id ?: return emptyList()
        logger.debug("Logged user is {}", userId)

        val workoutPlans = table.select {
            filter { eq("user_id", userId) }
        }.decodeList<WorkoutPlan>()
        logger.debug("Found {} workout plans for user {}", workoutPlans.size, userId)
        return workoutPlans
    }
}