package jerryppo.com.firebasechatting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.util.Log
import android.view.*
import android.widget.TextView
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_message_received.view.*
import kotlinx.android.synthetic.main.list_item.view.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {


    val SIGN_IN_REQUEST_CODE = 1

    val VIEW_TYPE_MESSAGE_SENT = 1
    val VIEW_TYPE_MESSAGE_RECEIVED = 2

    lateinit var myEmail: String
    lateinit var emojIconActions: EmojIconActions

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_sign_out) {
            AuthUI.getInstance().signOut(this).addOnCompleteListener {
                Snackbar.make(activity_main, "You have been signed out.", Snackbar.LENGTH_SHORT).show()
                finish()
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (SIGN_IN_REQUEST_CODE == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                Snackbar.make(activity_main, "Successfully signed in. Welcome!", Snackbar.LENGTH_SHORT).show()
                displayChatMessage()
            } else {
                Snackbar.make(activity_main, "We couldn't sign you in. Please try again", Snackbar.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emojIconActions = EmojIconActions(applicationContext, activity_main, emoji_button, emojicon_edit_text)
        emojIconActions.ShowEmojicon()

        summit_button.setOnClickListener {
            val firebaseReference = FirebaseDatabase.getInstance().reference
            firebaseReference.push().setValue(ChatMessage(emojicon_edit_text.text.toString(), myEmail))
            emojicon_edit_text.text = "".toEditable()
            emojicon_edit_text.requestFocus()
        }

        val currentUser = FirebaseAuth.getInstance().currentUser

        //check if not sign-in then nevigate Sign-in page
        if (null == currentUser) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE)
        } else {
            myEmail = currentUser.email!!
            Snackbar.make(activity_main, "Welcome " + myEmail, Snackbar.LENGTH_SHORT).show()
        }

        displayChatMessage()
    }

    private fun displayChatMessage() {

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = false
        layoutManager.stackFromEnd = true
        list_of_message.setHasFixedSize(true)
        list_of_message.layoutManager = layoutManager

        val firebaseReference = FirebaseDatabase.getInstance().reference

        val adapter = object : FirebaseRecyclerAdapter<ChatMessage,RecyclerView.ViewHolder>(ChatMessage::class.java,
            R.layout.item_message_sent,
            RecyclerView.ViewHolder::class.java,
            firebaseReference) {
            override fun populateViewHolder(viewHolder: RecyclerView.ViewHolder?, model: ChatMessage?, position: Int) {

                if (viewHolder is SentMessageHolder) {
                    viewHolder.bind(model!!)
                }
                else if (viewHolder is ReceivedMessageHolder){
                    viewHolder.bind(model!!)
                }
            }



            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
                if (VIEW_TYPE_MESSAGE_SENT== viewType) {
                    val view = LayoutInflater.from(applicationContext).inflate(R.layout.item_message_sent, parent,false)
                    return SentMessageHolder(view)
                }
                else {
                    val view = LayoutInflater.from(applicationContext).inflate(R.layout.item_message_received, parent,false)
                    return ReceivedMessageHolder(view)
                }

            }

            override fun getItemViewType(position: Int): Int {

                val model = getItem(position);

                if (model.messageUser == myEmail) {
                    return VIEW_TYPE_MESSAGE_SENT
                }
                else {
                    return VIEW_TYPE_MESSAGE_RECEIVED
                }

            }

        }

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                layoutManager.smoothScrollToPosition(list_of_message, null, adapter.getItemCount());
            }
        })

        list_of_message.adapter = adapter

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
