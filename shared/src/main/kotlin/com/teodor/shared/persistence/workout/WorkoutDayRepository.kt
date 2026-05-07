package com.teodor.shared.persistence.workout

import com.teodor.shared.domain.entities.WorkoutDay
import com.teodor.shared.persistence.Repository

interface WorkoutDayRepository : Repository<Long, WorkoutDay> {
}