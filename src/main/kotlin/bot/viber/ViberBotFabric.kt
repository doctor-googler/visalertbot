package bot.viber

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.common.base.Preconditions
import com.google.common.net.MediaType
import com.google.common.util.concurrent.Uninterruptibles
import com.viber.bot.Request
import com.viber.bot.ViberSignatureValidator
import com.viber.bot.api.MessageDestination
import com.viber.bot.message.TextMessage
import com.viber.bot.profile.BotProfile
import com.viber.bot.profile.UserProfile
import dao.ViberUserDao
import fi.iki.elonen.NanoHTTPD
import init.Resources
import model.ViberUser
import java.io.IOException
import java.util.*

class ViberBotFabric(viberUserDao: ViberUserDao) {
    private val nanoHttpd: NanoHTTPD
    private val token = "viber.bot.token"
    private val name = "viber.bot.name"
    private val avatar = "viber.bot.avatar"
    private val viberBot: ViberCustomBot
    private val signatureValidator: ViberSignatureValidator
    private val resources = Resources()

    init {
        val authToken = resources.getResourse<String>(token)
        viberBot = ViberCustomBot(BotProfile(resources.getResourse(name)), authToken)
        viberBot.onDatesReceived { message ->
            viberUserDao.getAll().forEach { viberBot.sendMessage(MessageDestination(constructUserProfile(it)),
                    TextMessage("New dates appeared:\n ${message.dates}")) }
        }
        viberBot.onMessageReceived { event, _, _ ->
            val usr = viberUserDao.getById(event.sender.id)
            when(usr) {
                null ->
                    viberUserDao.save(ViberUser(
                            null,
                            event.sender.id,
                            event?.chatId,
                            event.sender.name,
                            event.sender.avatar,
                            event.sender.country,
                            event.sender.language,
                            event.sender.apiVersion,
                            true
                    ))
            }
        }
        signatureValidator = ViberSignatureValidator(authToken)
        nanoHttpd = object: NanoHTTPD("localhost", 8086) {
            init {
                start(SOCKET_READ_TIMEOUT, false)
            }

            override fun serve(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
                val json = parsePostData(session).orEmpty()
                //val serverSideSignature = session.headers["x-viber-content-signature"].orEmpty()
                //Preconditions.checkState(signatureValidator.isSignatureValid(serverSideSignature, json), "invalid signature")

                val request = Request.fromJsonString(json)
                val inputStream = Uninterruptibles.getUninterruptibly(viberBot.incoming(request))

                return NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK, MediaType.JSON_UTF_8.toString(), inputStream)
            }

            private fun parsePostData(session: NanoHTTPD.IHTTPSession): String? {
                val body = HashMap<String, String>()
                try {
                    session.parseBody(body)
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: NanoHTTPD.ResponseException) {
                    e.printStackTrace()
                }

                return body["postData"]
            }
        }
        viberBot.setWebhook(url()).get()
    }

    fun generateBot(): ViberCustomBot {
        return this.viberBot
    }

    private fun url(): String {
        return "https://6a076f19.ngrok.io"
    }
    private fun constructUserProfile(user: ViberUser): UserProfile {
        val constructor = Class.forName("com.viber.bot.profile.UserProfile").declaredConstructors[0]
        constructor.isAccessible = true
        return constructor.newInstance(user.vberId, user.country, user.language, user.viberApiVersion, user.name, user.avatar) as UserProfile
    }
}