package com.cuervolu.witcherscodex.domain.models

data class Weapons(
    val weapons: List<Weapon>
)

data class Weapon(
    var entryId: String = "",
    val name: String = "",
    val base_damage: String = "",
    val bonuses: String? = null,
    val crafting_req: String? = null,
    var imageUrl: String = "",
    var author: String? = ""
)
