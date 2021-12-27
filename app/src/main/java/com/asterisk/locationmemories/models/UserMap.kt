package com.asterisk.locationmemories.models

import java.io.Serializable

data class UserMap(
    val title: String,
    val place: List<Place>
): Serializable