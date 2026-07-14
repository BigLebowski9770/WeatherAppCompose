package ru.umarsh.weatherappcompose.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "forecast_cache")
data class ForecastCacheEntity(
    @PrimaryKey
    val id: Int = SINGLE_CACHE_ID,
    val latitude: Double,
    val longitude: Double,
    val locationName: String,
    val responseJson: String,
    val updatedAtMillis: Long,
)

@Dao
interface ForecastCacheDao {

    @Query("SELECT * FROM forecast_cache WHERE id = :id LIMIT 1")
    suspend fun get(id: Int = SINGLE_CACHE_ID): ForecastCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ForecastCacheEntity)
}

private const val SINGLE_CACHE_ID = 1
