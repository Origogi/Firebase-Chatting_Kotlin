package jerryppo.com.firebasechatting

import java.util.*

class ChatMessage {

    var messageTime: Long
    var messageText: String
    var messageUser: String

    init {
        messageTime = Date().time
        messageText = ""
        messageUser = ""
    }

    constructor() {
    }

    constructor(text: String, email: String) {
        messageText = text
        messageUser = email
    }
}