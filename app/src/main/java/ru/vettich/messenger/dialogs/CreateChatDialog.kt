package ru.vettich.messenger.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatDialogFragment
import ru.vettich.messenger.R

class CreateChatDialog : AppCompatDialogFragment() {
    var listener: AddListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.dialog_create_chat, null)
        val usernameView = view.findViewById<EditText>(R.id.username_et)

        return builder.setView(view)
            .setTitle("Create new chat")
            .setNegativeButton("cancel") { dialogInterface, i -> }
            .setPositiveButton("add") { dialogInterface, i ->
                listener?.onAddChat(usernameView.text.toString())
            }
            .create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as AddListener
    }

    interface AddListener {
        fun onAddChat(username: String)
    }
}