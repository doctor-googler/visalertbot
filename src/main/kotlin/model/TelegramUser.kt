package model

class TelegramUser(val _id: String?,
                   val telegramId: String,
                   val chatId: String,
                   val name: String,
                   val avatar: String?,
                   val country: String,
                   val language: String,
                   var active: Boolean)
