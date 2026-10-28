package com.sundaytools.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application entry point. Annotated for Hilt dependency injection and configured
 * as a WorkManager Configuration.Provider so scheduled workers (e.g. the daily
 * archive job) can use Hilt-injected dependencies.
 */
@HiltAndroidApp
class SundayToolsApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
