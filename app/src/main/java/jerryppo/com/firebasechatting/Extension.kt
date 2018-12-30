package jerryppo.com.firebasechatting

import android.text.Editable

fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)
