package dao

import com.mongodb.MongoClient
import com.mongodb.client.MongoCollection
import init.Resources
import model.ViberUser
import org.litote.kmongo.findOne
import org.litote.kmongo.findOneById
import org.litote.kmongo.save

class ViberUserDao(mongoClient: MongoClient) {
    private val storage: MongoCollection<ViberUser>

    init {
        val rsc = Resources()
        storage = mongoClient
                .getDatabase(rsc.getResourse<String>("db.name"))
                .getCollection(rsc.getResourse<String>("db.collection.viber"), ViberUser::class.java)
    }

    fun save(user: ViberUser) {
        storage.save(user)
    }

    fun getAll(): List<ViberUser> {
        return storage.find().toList()
    }

    fun getById(id: String): ViberUser? {
        return storage.findOne("{\"viberId\":\"$id\"}")
    }

    fun suspend(id: String) {
        val user = storage.findOneById(id)
        if (user != null) {
            user.active = false
            storage.save(user)
        }
    }

}