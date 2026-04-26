package com.teodor.shared.domain.enums

import kotlinx.serialization.Serializable

@Serializable
enum class EquipmentType {
    MACHINE,
    DUMBBELL,
    BARBELL,
    BENCH,
    BODY_WEIGHT,
    EZ_BAR,
    CABLE_MACHINE,
    OTHER,
}