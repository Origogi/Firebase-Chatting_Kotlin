package jerryppo.com.firebasechatting

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.format.DateFormat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.database.FirebaseListAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_item.*
import kotlinx.android.synthetic.main.list_item.view.*
import java.util.*

class MainActivity : AppCompatActivity() {


    val SIGN_IN_REQUEST_CODE = 1
    lateinit var myEmail : String
    lateinit var adapter : FirebaseListAdapter<ChatMessage>

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
            }
            else {
                Snackbar.make(activity_main, "We couldn't sign you in. Please try again", Snackbar.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener {
            val firebaseReference = FirebaseDatabase.getInstance().reference
            firebaseReference.push().setValue(ChatMessage(input.text.toString(),myEmail ))
            input.text = "".toEditable()
        }

        val currentUser = FirebaseAuth.getInstance().currentUser

        //check if not sign-in then nevigate Sign-in page
        if (null == currentUser) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE)
        }
        else {
            myEmail = currentUser.email!!
            Snackbar.make(activity_main, "Welcome " + myEmail, Snackbar.LENGTH_SHORT).show()
        }

        displayChatMessage()
    }

    private fun displayChatMessage() {
        val firebaseReference = FirebaseDatabase.getInstance().reference


        adapter = object : FirebaseListAdapter<ChatMessage>(this,
            ChatMessage::class.java,
            R.layout.list_item,
            firebaseReference) {
            override fun populateView(v: View?, model: ChatMessage?, position: Int) {
                model?.run {

                    v?.run {
                        this.message_text.setText(messageText)
                        this.message_user.setText(messageUser)
                        this.message_time.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", messageTime))
                    }
                }
            }
        }

        list_of_message.adapter = adapter



    }
}
