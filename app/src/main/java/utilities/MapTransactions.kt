package utilities
import java.text.SimpleDateFormat


//Function to check for the good format of the date.
fun String.isValidDateFormat(format: String): Boolean {
        val sdf = SimpleDateFormat(format)
        sdf.isLenient = false
        return try {
            sdf.parse(this)
            true
        } catch (e: Exception) {
            false
        }
}
