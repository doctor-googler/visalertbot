package bot.viber

import Resources
import com.google.common.base.Preconditions
import com.google.common.net.MediaType
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.Uninterruptibles
import com.viber.bot.Request
import com.viber.bot.ViberSignatureValidator
import com.viber.bot.api.ViberBot
import com.viber.bot.message.Message
import com.viber.bot.message.TextMessage
import com.viber.bot.profile.BotProfile
import fi.iki.elonen.NanoHTTPD
import java.io.IOException
import java.util.*
import java.util.concurrent.ExecutionException


class ViberBotFabric(hostname: String?, port: Int) : NanoHTTPD(hostname, port) {
    private val token = "viber.bot.token"
    private val name = "viber.bot.name"
    private val avatar = "viber.bot.avatar"
    private val viberBot: ViberBot
    private val signatureValidator: ViberSignatureValidator
    private val resources = Resources()

    init {
        start(SOCKET_READ_TIMEOUT, false)
        val authToken = resources.getResourse<String>(token)
        viberBot = ViberBot(BotProfile(resources.getResourse(name)), authToken)
        signatureValidator = ViberSignatureValidator(authToken)
        viberBot.setWebhook("https://9856711b.ngrok.io").get()
        viberBot.onMessageReceived { _, message, response -> response.send(message) } // echos everything back
        viberBot.onConversationStarted { event ->
            Futures.immediateFuture(Optional.ofNullable(TextMessage("Hi " + event!!.user.name) as Message))
        }
    }

    fun generateBot(): ViberBot {
        return this.viberBot
    }

    override fun serve(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        try {
            val json = parsePostData(session).orEmpty()
            val serverSideSignature = session.headers["x-viber-content-signature"].orEmpty()
            Preconditions.checkState(signatureValidator.isSignatureValid(serverSideSignature, json), "invalid signature")

            val request = Request.fromJsonString(json)
            val inputStream = Uninterruptibles.getUninterruptibly(viberBot.incoming(request))

            return NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK, MediaType.JSON_UTF_8.toString(), inputStream)
        } catch (e: ExecutionException) {
            e.printStackTrace()
            return NanoHTTPD.newFixedLengthResponse("Error, sorry")
        }

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