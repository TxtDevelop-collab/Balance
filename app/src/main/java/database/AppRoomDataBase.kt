package database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import balance.TransactionsDao
import entities.balance.TransactionsData


//Although the app data base is not required, we have done the it to prepare the code for new
//functions.

@Database(
    exportSchema =  true,
    version = 1,
    entities = arrayOf(TransactionsData::class
    )

)
abstract class AppRoomDataBase :RoomDatabase(){
    abstract fun transacctions(): TransactionsDao
    companion object{

        @Volatile private var INSTANCE: AppRoomDataBase? = null
        fun getInstance(context: Context): AppRoomDataBase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                AppRoomDataBase::class.java, "Balance.db")
                // prepopulate the database after onCreate was called
                .addCallback(object : RoomDatabase.Callback() {
                })
                .fallbackToDestructiveMigration()
                .build()
    }
}