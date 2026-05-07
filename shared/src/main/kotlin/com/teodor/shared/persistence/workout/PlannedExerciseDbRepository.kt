package com.teodor.shared.persistence.workout

import com.teodor.shared.domain.entities.PlannedExercise
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import org.slf4j.LoggerFactory

class PlannedExerciseDbRepository(
    supabase: SupabaseClient
) : PlannedExerciseRepository {

    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        logger.debug("Initializing PlannedExerciseDbRepository")
    }

    private val table = supabase.from("planned_exercises")

    override suspend fun save(entity: PlannedExercise): PlannedExercise {
        logger.debug("Saving planned exercise: {}", entity)
        val savedPlannedExercise = table.insert(entity) {
            select()
        }.decodeSingle<PlannedExercise>()
        logger.debug("Planned exercise stored successfully: {}", savedPlannedExercise)
        return savedPlannedExercise
    }

    override suspend fun findById(id: Long): PlannedExercise? {
        logger.debug("Finding planned exercise with id {}", id)
        val foundExercise = table.select(
            columns = Columns.raw("*, exercises(*)")
        ) {
            filter { eq("id", id) }
        }.decodeSingleOrNull<PlannedExercise>()
        logger.debug("Planned exercise found: {}", foundExercise ?: "NOT FOUND")
        return foundExercise
    }

    override suspend fun update(entity: PlannedExercise): PlannedExercise? {
        logger.debug("Updating planned exercise with new values: {}", entity)
        val plannedExerciseId = entity.id ?: return null
        val updatedExercise = table.update(entity) {
            filter { eq("id", plannedExerciseId) }
            select()
        }.decodeSingleOrNull<PlannedExercise>()
        logger.debug("The updated planned exercise is: {}", updatedExercise)
        return updatedExercise
    }

    override suspend fun delete(id: Long): PlannedExercise? {
        logger.debug("Deleting planned exercise with id {}", id)

        val deletedExercise = table.delete {
            filter { eq("id", id) }
            select()
        }.decodeSingleOrNull<PlannedExercise>()
        logger.debug("Deleted planned exercise: {}", deletedExercise)
        return deletedExercise
    }
}