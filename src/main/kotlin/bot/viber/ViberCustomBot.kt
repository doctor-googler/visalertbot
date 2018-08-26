package bot.viber;

import com.viber.bot.api.ViberBot
import com.viber.bot.profile.BotProfile
import messaging.Message

class ViberCustomBot(profile: BotProfile, authToken: String) : ViberBot(profile, authToken) {
    private var callBack: ((Message) -> Unit)? = null

    fun onDatesReceived(callBack: (Message) -> Unit) {
        this.callBack = callBack
    }

    fun datesReceived(msg: Message) {
        this.callBack?.invoke(msg)
    }
}
