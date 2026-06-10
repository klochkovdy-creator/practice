package ci.nsu.mobile.main.di

import android.content.Context
import ci.nsu.mobile.main.data.local.AppDatabase
import ci.nsu.mobile.main.data.repository.AuthRepository
import ci.nsu.mobile.main.data.repository.DepositRepository

object ServiceLocator {

    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(appContext)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository()
    }

    val depositRepository: DepositRepository by lazy {
        DepositRepository(database)
    }
}
