package be.mobilesecurity.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AppointmentRepository(private val appointmentDao: AppointmentDao) {

    fun getAllAppointments(): Flow<List<AppointmentEntity>> {
        return appointmentDao.getAllAppointments()
    }

    suspend fun insertAppointment(appointment: AppointmentEntity) {
        withContext(Dispatchers.IO) {
            appointmentDao.insertAppointment(appointment)
        }
    }
}
