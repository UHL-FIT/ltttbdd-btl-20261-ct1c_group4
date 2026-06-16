package com.example.pokedex.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "pokemon")
data class Pokemon(
    @PrimaryKey val id: String, // Đổi sang String để hỗ trợ 6.1, 6.2
    val name: String,
    val types: List<String>,
    val hp: Int,
    val attack: Int,
    val defense: Int,
    val spAtk: Int,
    val spDef: Int,
    val speed: Int,
    val height: Int = 0,
    val weight: Int = 0,
    val imageUrl: String,
    val description: String = "",
    val abilities: List<AbilityInfo>,
    val evolutionChain: List<EvolutionStep> = emptyList(),
    val moves: List<MoveInfo> = emptyList(),
    val pveTier: String? = null,
    val pveRole: String? = null,
    val pvp1v1Tier: String? = null,
    val pvp1v1Role: String? = null,
    val pvp2v2Tier: String? = null,
    val pvp2v2Role: String? = null,
    val tags: List<String> = emptyList(),
    val sortOrder: Double = 0.0,
    val hasFullDetails: Boolean = false
) {
    val total: Int get() = hp + attack + defense + spAtk + spDef + speed

    // Computed properties to avoid serialization issues with lazy delegates
    val baseName: String 
        get() = name.substringBefore(" Mega").substringBefore(" Alola")
            .substringBefore(" Galar").substringBefore(" Hisui")
            .substringBefore(" Paldea").substringBefore(" Black")
            .substringBefore(" White").substringBefore(" Dusk")
            .substringBefore(" Dawn").substringBefore(" Ultra")
            .substringBefore(" Origin").substringBefore(" Primal")
            .substringBefore(" Crowned").substringBefore(" Ice")
            .substringBefore(" Shadow").substringBefore(" Therian")
            .substringBefore(" Unbound").substringBefore(" Complete")

    val variantName: String 
        get() = if (name.contains(baseName)) name.substringAfter(baseName).trim() else ""
    
    // Hiển thị ID gốc (bỏ phần thập phân nếu có)
    val formattedId: String 
        get() {
            val baseId = id.split(".")[0]
            return baseId.padStart(4, '0')
        }
}

data class EvolutionStep(
    val id: String,
    val name: String,
    val imageUrl: String,
    val condition: String = "",
    val evolvesFrom: String? = null // Tên pokemon tiến hóa từ
)

data class AbilityInfo(
    val name: String,
    val effect: String = ""
)

data class MoveInfo(
    val name: String,
    val type: String,
    val category: String, // Physical, Special, Status
    val power: Int?,
    val accuracy: Int?,
    val priority: Int = 0,
    val description: String = "",
    val learnMethod: String = "level-up",
    val level: Int = 0,
    val learnDetail: String = "" // e.g., TM name or "Egg"
)

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: List<String>?): String = gson.toJson(value ?: emptyList<String>())

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromEvolutionList(value: List<EvolutionStep>?): String = gson.toJson(value ?: emptyList<EvolutionStep>())

    @TypeConverter
    fun toEvolutionList(value: String): List<EvolutionStep> {
        val listType = object : TypeToken<List<EvolutionStep>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromMoveList(value: List<MoveInfo>?): String = gson.toJson(value ?: emptyList<MoveInfo>())

    @TypeConverter
    fun toMoveList(value: String): List<MoveInfo> {
        val listType = object : TypeToken<List<MoveInfo>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromAbilityList(value: List<AbilityInfo>?): String = gson.toJson(value ?: emptyList<AbilityInfo>())

    @TypeConverter
    fun toAbilityList(value: String): List<AbilityInfo> {
        return try {
            val listType = object : TypeToken<List<AbilityInfo>>() {}.type
            gson.fromJson(value, listType) ?: emptyList()
        } catch (e: Exception) {
            // Fallback for legacy data where abilities were List<String>
            try {
                val stringListType = object : TypeToken<List<String>>() {}.type
                val stringList: List<String> = gson.fromJson(value, stringListType) ?: emptyList()
                stringList.map { AbilityInfo(name = it, effect = "") }
            } catch (e2: Exception) {
                emptyList()
            }
        }
    }
}
