package com.example.balance

import adapters.BalanceAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.balance.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import utilities.NetworkStatus

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var binding:ActivityMainBinding? = null
    private val fbinding:ActivityMainBinding get() = binding!!
    private val viewModel:viewmodels.Mainviewmodel by viewModels()
    lateinit var bar:ProgressBar
    lateinit var adapter1: BalanceAdapter
    private var connectivity:Boolean=true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        bar = fbinding.progressBar
        adapter1 = BalanceAdapter()
        val linear = LinearLayoutManager(this)
        fbinding.recyclerView.apply {
            this.layoutManager = linear
            this.adapter = adapter1
        }
        fbinding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE ) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    if (firstVisibleItemPosition == 0) {
                        getData()
                    }
                }
            }
        })
        registerObserverBalanceData()
        registerInternetConnection()
        getData()
        bar.visibility=View.GONE
        fbinding.warnning.visibility = View.GONE
        fbinding.textView.visibility=View.GONE
        setContentView(fbinding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mainactivitymenu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when(id){
            R.id.sinc ->{
                handleConnectivityScreen(true)
                getData()
            }
            else->{
            }
        }
        return super.onOptionsItemSelected(item)
    }
    fun getData() {
            setScreenConditions("LOADING")
            viewModel.getMyBalance()
            //handleConnectivityScreen(connectivity)
    }
    private fun registerObserverBalanceData(){
        lifecycleScope.launch {
                    viewModel.balancedata.observe(this@MainActivity,Observer{
                        when(it.status){
                            utilities.Result.Status.SUCCESS ->{
                                adapter1.setData(it.data!!.toMutableList())
                                setScreenConditions("DONE")
                            }
                            utilities.Result.Status.ERROR ->{
                                fbinding.textView.text=it.message.toString()
                                handleConnectivityScreen(false,"NOT INTERNET.")
                            }
                            utilities.Result.Status.LOADING->{
                                //TODO CORRECTO
                            }
                            else -> {
                                //No implementamos nada porque esta todo ok.
                            }
                        }
                    })
            }
    }
    private fun registerInternetConnection(){
        lifecycleScope.launch {
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED){
                    viewModel.internetstatus.collect{
                        if(it==  NetworkStatus.Status.NOT_CONNECTED){
                            //handleConnectivityScreen(false,"NOT INTERNET,CHECK CONNECTIVITY")
                            connectivity=false
                        }else if(it==NetworkStatus.Status.COONECTED){
                            if(fbinding.warnning.visibility==View.VISIBLE&&
                                fbinding.recyclerView.adapter!!.itemCount==0)
                                getData()
                            handleConnectivityScreen(true)
                            setScreenConditions("DONE")
                            connectivity=true
                        }
                    }
                }
            }
        }
    }
    private fun setScreenConditions(type:String){
        when(type){
            "LOADING" ->{
                fbinding.progressBar.visibility=View.VISIBLE
                fbinding.recyclerView.alpha=0.25f
            }
            "DONE"->{
                fbinding.progressBar.visibility=View.GONE
                fbinding.recyclerView.alpha=1f
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.balancedata.removeObservers(this)
        binding=null
    }
    fun handleConnectivityScreen(isConnected:Boolean,message:String=""){
        when(isConnected){
            true->{
                if(fbinding.warnning.visibility==View.VISIBLE) fbinding.warnning.visibility=View.GONE
                fbinding.recyclerView.visibility=View.VISIBLE
                fbinding.textView.visibility=View.INVISIBLE
                fbinding.recyclerView.isEnabled=true
            }
            false->{
                if(message!="") fbinding.textView.text=message
                if(fbinding.progressBar.visibility==View.VISIBLE) fbinding.progressBar.visibility=View.GONE
                fbinding.textView.visibility=View.VISIBLE
                fbinding.warnning.visibility=View.VISIBLE
                fbinding.recyclerView.visibility=View.INVISIBLE
                fbinding.recyclerView.isEnabled=false
            }
        }
    }
}