package com.teodor.shared.persistence.workout

import com.teodor.shared.domain.entities.WorkoutPlan
import com.teodor.shared.persistence.Repository

interface WorkoutPlanRepository : Repository<Long, WorkoutPlan> {
    suspend fun findAllForUser(): Iterable<WorkoutPlan>
}