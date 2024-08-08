package be.mobilesecurity.data
import androidx.room.Database
import androidx.room.RoomDatabase
@Database(entities = [AppointmentEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appointmentDao(): AppointmentDao
}
