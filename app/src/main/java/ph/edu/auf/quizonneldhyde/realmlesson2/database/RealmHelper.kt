package ph.edu.auf.quizonneldhyde.realmlesson2.database

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.selects.SelectInstance
import ph.edu.auf.quizonneldhyde.realmlesson2.database.realmmodel.OwnerModel
import ph.edu.auf.quizonneldhyde.realmlesson2.database.realmmodel.PetModel

object RealmHelper {

    private lateinit var  realmInstance: Realm

    fun initializeRealm(){
        val config = RealmConfiguration.Builder(schema = setOf(PetModel::class, OwnerModel::class))
            .name("petrealm.realm")
            .schemaVersion(1)
            .initialData {
                copyToRealm(PetModel().apply {
                    name = "Fido"
                    petType = "Dog"
                    age = 3
                })
                copyToRealm(PetModel().apply {
                    name = "Whiskers"
                    petType = "Cat"
                    age = 2
                })

                copyToRealm(OwnerModel().apply {
                    name = "Talya"

                })
                copyToRealm(OwnerModel().apply {
                    name = "John"
                    pets.addAll(
                        listOf(
                            PetModel().apply {
                                name = "Choco"
                                petType = "Aspin"
                                age = 5
                            },
                        )
                    )
                })
            }
            .build()

        realmInstance = Realm.open(config)

    }

    fun getRealmInstance() : Realm {
        if (!::realmInstance.isInitialized) {
            throw IllegalStateException("Realm instance not initialized. Call initializeRealm() first.")
        }
        return realmInstance

    }

    fun closeRealm() {
        if (::realmInstance.isInitialized && !realmInstance.isClosed()) {
            realmInstance.close()
        }
    }
}
