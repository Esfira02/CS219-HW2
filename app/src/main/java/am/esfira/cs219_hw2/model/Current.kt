package am.esfira.cs219_hw2.model

data class Current (
    val temp_c: Float,
    val temp_f: Float,
    val humidity: Int,
    val condition: Condition
)