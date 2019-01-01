package jerryppo.com.firebasechatting

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.item_message_received.view.*

class MessagelistAdapter(val context: Context, val myAccount: String, val messages: MutableList<ChatMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val VIEW_TYPE_MESSAGE_SENT = 1
    val VIEW_TYPE_MESSAGE_RECEIVED = 2


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_message_sent, parent, false)
            return SentMessageHolder(view)
        }
        else {
            val view = LayoutInflater.from(context).inflate(R.layout.item_message_received, parent, false)
            return ReceivedMessageHolder(view)
        }

    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {

        val chatMessage = messages[position]

        when (holder?.itemViewType) {
            VIEW_TYPE_MESSAGE_SENT-> (holder as SentMessageHolder).bind(chatMessage)
            VIEW_TYPE_MESSAGE_RECEIVED -> (holder as SentMessageHolder).bind(chatMessage)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val chatMessage = messages[position]
        if (chatMessage.messageUser == myAccount) {
            return VIEW_TYPE_MESSAGE_SENT
        }
        else {
            return VIEW_TYPE_MESSAGE_RECEIVED
        }
    }

    class SentMessageHolder(item: View) : RecyclerView.ViewHolder(item) {

        val messageText: TextView
        val time: TextView

        init {
            item.run {
                messageText = text_message_body
                time = text_message_time
            }
        }

        fun bind(chatMessage: ChatMessage) {
            messageText.text = chatMessage.messageText
            time.setText(DateFormat.format("HH:mm:ss", chatMessage.messageTime))
        }
    }

    class ReceivedMessageHolder(item: View) : RecyclerView.ViewHolder(item) {

        val messageText: TextView
        val time: TextView
        val name: TextView

        init {
            item.run {
                messageText = text_message_body
                name = text_message_name
                time = text_message_time
            }
        }

        fun bind(chatMessage: ChatMessage) {
            messageText.text = chatMessage.messageText
            time.setText(DateFormat.format("HH:mm:ss", chatMessage.messageTime))
            name.setText(chatMessage.messageUser)

        }

    }
}