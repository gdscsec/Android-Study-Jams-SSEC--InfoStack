package com.example.infostack

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.infostack.R
import android.content.Intent
import android.view.View
import android.widget.*
import com.example.infostack.staffregister
import com.example.infostack.studentlogin
import com.example.infostack.stafflogin
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.example.infostack.staffdashboard
import com.google.firebase.database.DatabaseError
import java.util.HashMap
import java.util.regex.Pattern

class stafflogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stafflogin)
        val signup = findViewById<View>(R.id.sign_up_txt_staff) as TextView
        signup.setOnClickListener { // click handling code
            startActivity(Intent(this@stafflogin, staffregister::class.java))
        }
        val backarrow = findViewById<View>(R.id.back_arrow_staff) as ImageView
        backarrow.setOnClickListener { // click handling code
            startActivity(Intent(this@stafflogin, studentlogin::class.java))
        }
        val sign = findViewById<View>(R.id.sign_in_btn_staff) as Button
        sign.setOnClickListener(View.OnClickListener {
            val mail = findViewById<View>(R.id.mail_id_edit_txt_staff) as EditText
            val pass = findViewById<View>(R.id.password_edit_txt_staff) as EditText
            val userEmail = mail.text.toString().trim { it <= ' ' }
            val userPass = pass.text.toString().trim { it <= ' ' }
            if (!isValidEmail(userEmail)) {
                mail.error = "Email cannot be Empty"
                Toast.makeText(applicationContext, "Enter Valid Email ID", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            if (userPass == "") {
                pass.error = "Password cannot be Empty"
                Toast.makeText(applicationContext, "Password cannot be Empty", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            val databaseReference = FirebaseDatabase.getInstance().getReference("staff")
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.value as HashMap<String, Any>?
                    if (value!!.containsKey(getDBUserName(userEmail))) {
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(userEmail, userPass).addOnCompleteListener(this@stafflogin) { task -> // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful) {
                                Toast.makeText(applicationContext, "Invalid Login Credentials", Toast.LENGTH_SHORT).show()
                            } else {
                                val intent = Intent(this@stafflogin, staffdashboard::class.java)
                                startActivity(intent)
                                Toast.makeText(applicationContext, "Logged In Successfullty", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                    } else {
                        Toast.makeText(this@stafflogin, "User Does Not Exist", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        })
    }

    override fun onBackPressed() {
        startActivity(Intent(this@stafflogin, studentlogin::class.java))
        finish()
    }

    companion object {
        fun isValidEmail(email: String?): Boolean {
            val emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$"
            val pat = Pattern.compile(emailRegex)
            return if (email == null) false else pat.matcher(email).matches()
        }

        fun getDBUserName(S: String): String {
            var result = ""
            for (ch in S.toCharArray()) {
                if (ch == '@') {
                    break
                }
                result += ch
            }
            return result
        }
    }
}