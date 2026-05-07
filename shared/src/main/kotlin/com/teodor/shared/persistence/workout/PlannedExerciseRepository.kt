package com.teodor.shared.persistence.workout

import com.teodor.shared.domain.entities.PlannedExercise
import com.teodor.shared.persistence.Repository

interface PlannedExerciseRepository : Repository<Long, PlannedExercise> {
}