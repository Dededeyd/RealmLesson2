package ph.edu.auf.quizonneldhyde.realmlesson2

import android.app.Application
import ph.edu.auf.quizonneldhyde.realmlesson2.database.RealmHelper

class RealmLessonApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RealmHelper.initializeRealm()
    }

    override fun onTerminate() {
        super.onTerminate()
        RealmHelper.closeRealm()
    }
}