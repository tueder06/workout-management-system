package com.teodor.shared.persistence.workout

import com.teodor.shared.domain.entities.WorkoutDay
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import org.slf4j.LoggerFactory

class WorkoutDayDbRepository(
    supabase: SupabaseClient
) : WorkoutDayRepository {

    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        logger.debug("Initializing WorkoutDayDbRepository")
    }

    private val table = supabase.from("workout_days")

    override suspend fun save(entity: WorkoutDay): WorkoutDay {
        logger.debug("Saving workout day: {}", entity)
        val savedWorkoutDay = table.insert(entity) {
            select()
        }.decodeSingle<WorkoutDay>()
        logger.debug("Workout day stored successfully: {}", savedWorkoutDay)
        return savedWorkoutDay
    }

    override suspend fun findById(id: Long): WorkoutDay? {
        logger.debug("Finding workout day with id {}", id)
        val foundWorkoutDay = table.select(
            columns = Columns.raw("*, planned_exercises(*, exercises(*))")
        ){
            filter { eq("id", id) }
        }.decodeSingleOrNull<WorkoutDay>()

        logger.debug("Workout day found: {}", foundWorkoutDay ?: "NOT FOUND")
        logger.debug("Loaded {} planned exercises for this workout day", foundWorkoutDay?.exercises?.size ?: 0)
        return foundWorkoutDay
    }

    override suspend fun update(entity: WorkoutDay): WorkoutDay? {
        logger.debug("Updating workout day with new values: {}", entity)
        val workoutDayId = entity.id ?: return null
        val updateWorkoutDay = table.update(entity) {
            filter { eq("id", workoutDayId) }
            select()
        }.decodeSingleOrNull<WorkoutDay>()
        logger.debug("The update workout day is: {}", updateWorkoutDay)
        return updateWorkoutDay
    }

    override suspend fun delete(id: Long): WorkoutDay? {
        logger.debug("Deleting workout day with id {}", id)
        val deletedWorkoutDay = table.delete {
            filter { eq("id", id) }
            select()
        }.decodeSingleOrNull<WorkoutDay>()
        logger.debug("Deleted workout day: {}", deletedWorkoutDay)
        return deletedWorkoutDay
    }
}