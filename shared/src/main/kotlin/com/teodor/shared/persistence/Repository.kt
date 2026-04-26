package com.teodor.shared.persistence

import com.teodor.shared.domain.entities.Entity

interface Repository<ID, E : Entity<ID>> {
    suspend fun save(entity: E): E
    suspend fun findById(id: ID): E?
    suspend fun update(entity: E): E?
    suspend fun delete(id: ID): E?
}