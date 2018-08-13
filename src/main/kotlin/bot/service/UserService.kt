package bot.service

import com.mongodb.client.MongoCollection
import org.bson.Document
import org.litote.kmongo.KMongo
import org.litote.kmongo.find
import org.litote.kmongo.insertOne

class UserService {
    private val storage: MongoCollection<Document> = KMongo
            .createClient("localhost")
            .getDatabase("visalert")
            .getCollection("users")

    fun addUser(chatId: String, name: String) {
        storage.insertOne("{ chatId: $chatId, name: $name }")
    }
    fun getAll() {
        print(storage.find("{}"))
    }

}