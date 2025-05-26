package es.tecnicalman.utils.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update

@Dao
interface TareaDao {
    @Query("SELECT * FROM tareas")
    suspend fun getAll(): List<TareaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tareas: List<TareaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tarea: TareaEntity)

    @Update
    suspend fun update(tarea: TareaEntity)

    @Delete
    suspend fun delete(tarea: TareaEntity)

    @Query("DELETE FROM tareas")
    suspend fun clearAll()
}