package be.mobilesecurity.data
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAppointment(appointment: AppointmentEntity)

    @Query("SELECT * FROM appointments")
    fun getAllAppointments(): Flow<List<AppointmentEntity>>
}
