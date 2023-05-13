package entities.balance

import com.google.gson.annotations.SerializedName

//Class that is used to serialized the service output.
data class Transactions(
    @SerializedName("id")
    var id: String?,

    @SerializedName("date")
    var date: String?,

    @SerializedName("amount")
    var amount: Double?,

    @SerializedName("fee")
    var fee: Double?=0.0,

    @SerializedName("description")
    var description:String?
)

