package adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.R
import entities.balance.Transactions


//Simple adapter, as far as the data is not to large, we didnt use the DiffUtil,
//if our data increase in size, we should change it.
//Also we have prepared the onClick event, with this we could do operations related to a specifict
//transaction
//we just have to introduce this code in the activity
/*
*   adapterInstance.setOnClickListener(object : BalanceAdapter.OnClickListener{
            override fun onClick(position: Int, transac:transaction) {
                Toast.makeText(this@MainActivity,transac.description,Toast.LENGTH_SHORT).show()
            }
        })*/
class BalanceAdapter:RecyclerView.Adapter<BalanceAdapter.BalanceViewHolder>() {
    private var balanceList = mutableListOf<Transactions>()
    private var onClickListener: OnClickListener? = null
    interface OnClickListener {
        fun onClick(position: Int, trs: Transactions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BalanceViewHolder {
       return BalanceViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.dispositivos_layout,parent,false ))
    }

    override fun onBindViewHolder(holder: BalanceViewHolder, position: Int) {
        holder.itemView.setOnClickListener{
            if(onClickListener!=null){
                onClickListener!!.onClick(position,balanceList.get(position))
            }
        }
        if(position==0){
            holder.itemView.findViewById<CardView>(R.id.cardView).setBackgroundResource(R.drawable.border)
        }
        holder.bind(balanceList.get(position))
    }

    override fun getItemCount()=balanceList.size

    inner class BalanceViewHolder(view:View): baseholder(view){
        private var idText:TextView = view.findViewById(R.id.id)
        private var amunt:TextView = view.findViewById(R.id.amount)
        private var imageView:ImageView = view.findViewById(R.id.settick)
        @SuppressLint("SetTextI18n")
        override fun bind(transacction: Transactions) {
            amunt.text = "${transacction.amount}\u20AC"
            val feeconverter = transacction.fee?: 0.0
            idText.text = "${feeconverter}\u20AC" + " & " + transacction.date
            when(transacction.amount.toString().contains("-")){
                true->{
                    imageView.setImageResource(R.drawable.redtick)
                }
                false->{
                    imageView.setImageResource(R.drawable.greentick)
                }
            }
        }
    }
    open class baseholder (view: View) : RecyclerView.ViewHolder(view){
        open fun bind(transacction:Transactions) {}
    }
    @SuppressLint("NotifyDataSetChanged")
    fun setData(data:MutableList<Transactions>) {
        data.let {
            balanceList.clear()
            this.balanceList.addAll(data)
        }
        notifyDataSetChanged()
    }
}