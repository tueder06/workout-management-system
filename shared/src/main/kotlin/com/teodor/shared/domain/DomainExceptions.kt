package com.teodor.shared.domain

class ValidationException(message: String, cause: Throwable? = null):
    Exception(message, cause)

class ValueNotFoundException(message: String, cause: Throwable? = null):
    Exception(message, cause)

class DuplicateValueException(message: String, cause: Throwable? = null):
    Exception(message, cause)

class InvalidCredentialsException(message: String, cause: Throwable? = null):
    Exception(message, cause)