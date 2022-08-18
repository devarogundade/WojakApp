package dev.arogundade.wojak.storage.dao

import androidx.room.*
import dev.arogundade.wojak.models.Metadata
import kotlinx.coroutines.flow.Flow

@Dao
interface MetadataDao {

    @Query("select * from metadata where id == :id limit 1")
    fun getMetadata(id: String): Flow<Metadata?>

    @Insert(entity = Metadata::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(metadata: Metadata)

    @Update(entity = Metadata::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(metadata: Metadata)

    @Delete(entity = Metadata::class)
    suspend fun delete(metadata: Metadata)

}