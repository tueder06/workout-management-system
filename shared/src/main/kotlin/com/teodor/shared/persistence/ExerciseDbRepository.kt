package com.teodor.shared.persistence

import com.teodor.shared.domain.entities.Exercise
import com.teodor.shared.domain.ExerciseFilter
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import org.slf4j.LoggerFactory

class ExerciseDbRepository(
    private val supabase: SupabaseClient
) : ExerciseRepository {

    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        logger.debug("Initializing ExerciseDbRepository")
    }
    private val table = supabase.from("exercises")

    override suspend fun save(entity: Exercise): Exercise {
        logger.debug("Saving exercise: {}", entity)
        val userId = supabase.auth.currentUserOrNull()?.id
        logger.debug("Logged user is {}", userId)
        val exerciseToSave = entity.copy(userId = userId)
        val savedExercise = table.insert(exerciseToSave) {
            select()
        }.decodeSingle<Exercise>()
        logger.debug("Exercised stored successfully: {}", savedExercise)
        return savedExercise
    }

    override suspend fun findById(id: Long): Exercise? {
        logger.debug("Finding exercise with id {}", id)
        val foundExercise = table.select {
            filter { eq("id", id) }
        }.decodeSingleOrNull<Exercise>()
        logger.debug("Exercise found: {}", foundExercise ?: "NOT FOUND")
        return foundExercise
    }

    override suspend fun update(entity: Exercise): Exercise? {
        logger.debug("Updating exercise with new values: {}", entity)
        val exerciseId = entity.id ?: return null
        val updatedExercise = table.update(entity) {
            filter { eq("id", exerciseId) }
            select()
        }.decodeSingleOrNull<Exercise>()
        logger.debug("The updated exercise is: {}", updatedExercise ?: "NOT FOUND")
        return updatedExercise
    }

    override suspend fun delete(id: Long): Exercise? {
        logger.debug("Deleting exercise with id {}", id)
        val exerciseToDelete = findById(id) ?: return null

        table.delete {
            filter { eq("id", id) }
        }
        logger.debug("Deleted exercise: {}", exerciseToDelete)
        return exerciseToDelete
    }

    override suspend fun filterExercises(filter: ExerciseFilter): Iterable<Exercise> {
        logger.debug("Searching for exercises using the filter: {}", filter)
        val userId = supabase.auth.currentUserOrNull()?.id
        logger.debug("Logged user is {}", userId)
        val updatedFilter = filter.copy(userId = userId)
        val list1 = supabase.from("exercises").select {
            filter {
                updatedFilter.nameSearchQuery?.let { searchQuery ->
                    if(searchQuery.isNotBlank()) {
                        ilike("name", "%$searchQuery%")
                    }
                }
                updatedFilter.muscleGroup?.let { muscle ->
                    eq("main_muscle_group", muscle.name)
                }
                updatedFilter.equipmentType?.let { equipment ->
                    contains("equipment_used", listOf(equipment.name))
                }
                updatedFilter.exerciseType?.let { exerciseType ->
                    eq("exercise_type", exerciseType.name)
                }
                if (updatedFilter.userId != null) {
                    or {
                        exact("user_id", null)
                        eq("user_id", updatedFilter.userId)
                    }
                } else {
                    exact("user_id", null)
                }
            }
        }.decodeList<Exercise>()
        val list2 = supabase.from("exercises").select {
            filter {
                updatedFilter.nameSearchQuery?.let { searchQuery ->
                    if(searchQuery.isNotBlank()) {
                        ilike("name", "%$searchQuery%")
                    }
                }
                updatedFilter.muscleGroup?.let { muscle ->
                    contains("secondary_muscle_groups", listOf(muscle.name))
                }
                updatedFilter.equipmentType?.let { equipment ->
                    contains("equipment_used", listOf(equipment.name))
                }
                updatedFilter.exerciseType?.let { exerciseType ->
                    eq("exercise_type", exerciseType.name)
                }
                if (updatedFilter.userId != null) {
                    or {
                        exact("user_id", null)
                        eq("user_id", updatedFilter.userId)
                    }
                } else {
                    exact("user_id", null)
                }
            }
        }.decodeList<Exercise>()
        val finalExercises = (list1 + list2).distinctBy { it.id }
        logger.debug("{} exercises have been found", finalExercises.size)
        return finalExercises
    }
}