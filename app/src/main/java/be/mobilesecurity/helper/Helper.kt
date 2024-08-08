import android.app.Activity
import android.graphics.Bitmap
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.util.Date

class Helper(private val activity: Activity) {

    fun takeScreenshot(): Pair<File, String> {
        val now = Date()
        val date = android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now)

        val v1 = activity.window.decorView.rootView
        v1.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(v1.drawingCache)
        v1.isDrawingCacheEnabled = false

        val imageFile = File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "screenshot-$date.png")

        val outputStream = FileOutputStream(imageFile)
        val quality = 100
        bitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream)
        outputStream.flush()
        outputStream.close()

        return Pair(imageFile, imageFile.path)
    }
}
