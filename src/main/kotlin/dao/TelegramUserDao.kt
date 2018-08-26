package dao

import com.mongodb.MongoClient
import com.mongodb.client.MongoCollection
import init.Resources
import model.TelegramUser
import org.litote.kmongo.findOne
import org.litote.kmongo.findOneById
import org.litote.kmongo.save

class TelegramUserDao(mongoClient: MongoClient) {
    private val storage: MongoCollection<TelegramUser>

    init {
        val rsc = Resources()
        storage = mongoClient
                .getDatabase(rsc.getResourse<String>("db.name"))
                .getCollection(rsc.getResourse<String>("db.collection.telegram"), TelegramUser::class.java)
    }

    fun save(user: TelegramUser) {
        storage.save(user)
    }

    fun getAll(): List<TelegramUser> {
        return storage.find().toList()
    }

    fun getById(id: String): TelegramUser? {
        return storage.findOne("{\"telegramId\":\"$id\"}")
    }

    fun suspend(id: String) {
        val user = storage.findOneById(id)
        if (user != null) {
            user.active = false
            storage.save(user)
        }
    }

}