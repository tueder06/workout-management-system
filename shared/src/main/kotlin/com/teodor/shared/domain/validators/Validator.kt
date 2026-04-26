package com.teodor.shared.domain.validators

interface Validator<E> {
    fun validate(entity: E): Result<Unit>
}