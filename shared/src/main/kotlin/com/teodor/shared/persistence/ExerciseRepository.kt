package com.teodor.shared.persistence

import com.teodor.shared.domain.entities.Exercise
import com.teodor.shared.domain.ExerciseFilter

interface ExerciseRepository : Repository<Long, Exercise> {
    suspend fun filterExercises(filter: ExerciseFilter): Iterable<Exercise>
}