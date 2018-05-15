package broker.impl

import broker.api.Message

class ScheduleMessage : Message<Map<String, List<String>>> {
    var msg: Map<String, List<String>> = HashMap()

    constructor(msg: Map<String, List<String>>) {
        this.msg = msg
    }

    override fun getVal(): Map<String, List<String>> {
        return msg;
    }

    override fun setVal(msg: Map<String, List<String>>) {
        this.msg = msg
    }

}