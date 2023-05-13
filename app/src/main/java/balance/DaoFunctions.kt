package balance

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import entities.balance.TransactionsData

//Dao functions are prepared to be used with the DataBase
@Dao
interface TransactionsDao{
    @Query("SELECT * FROM TRANSACTIONS")
    fun getTransactions():List<TransactionsData>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun  insertTransactions(recg:List<TransactionsData>)
}