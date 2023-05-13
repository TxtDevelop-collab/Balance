package entities.balance

import androidx.room.Entity
import androidx.room.PrimaryKey


//Here we save all the Entities that would be required

@Entity(tableName = "TRANSACTIONS")
data class TransactionsData (
    @PrimaryKey
    var id:String,
    var date: String?,
    var amount: Double?,
    var fee: Double?,
    var description:String?

    )