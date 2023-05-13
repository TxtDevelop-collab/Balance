package hiltinyecction

import android.content.Context
import android.net.ConnectivityManager
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import database.AppRoomDataBase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import service.ApiService
import utilities.DispatcherProvider
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


/*
* Here we create the appModule where we are gonna create all the dependencies that are
* inyectable.
*/

@Module
@InstallIn(SingletonComponent::class)
class appmodule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client2 = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .build()
        val baseUrl = "https://code-challenge-e9f47.web.app"
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client2)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }
    @Singleton
    @Provides
    fun providesDataBase(@ApplicationContext app: Context) = Room.databaseBuilder(app,
        AppRoomDataBase::class.java, "TXT.db")
        // prepopulate the database after onCreate was called
        .addCallback(object : RoomDatabase.Callback() {
        })
        .fallbackToDestructiveMigration()
        .build()

    @Singleton
    @Provides
    fun provideUserDao(db: AppRoomDataBase) = db.transacctions()
    //Here we create a dispatcherProvider that will allow us to inyect the Dispatchers to our classes
    //this will allow us to test the corutines in a better way from Unit Test.
    @Provides
    fun provideDispatcherProvider(): DispatcherProvider = DispatcherProvider()

    @Provides
    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @Provides
    @Singleton
    fun providesmyapiservice(retrofit: Retrofit):ApiService = retrofit.create(ApiService::class.java)
}