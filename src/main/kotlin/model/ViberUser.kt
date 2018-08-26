package model

class ViberUser(val _id: String?,
                val vberId: String,
                val chatId: String?,
                val name: String?,
                val avatar: String?,
                val country: String,
                val language: String,
                val viberApiVersion: Int?,
                var active: Boolean)