package com.rak12.gymapp.Activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.rak12.gymapp.R
import com.rak12.gymapp.Util.ConnectionManager
import com.rak12.gymapp.Util.Validations
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    lateinit var etmobile: EditText
    lateinit var etpass: EditText
    lateinit var fp: TextView
    lateinit var noacc: TextView
    lateinit var login: Button
    lateinit var skip:TextView
    lateinit var sp: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        etmobile = findViewById(R.id.mobile)
        etpass = findViewById(R.id.pass)
        login = findViewById(R.id.login)
        fp = findViewById(R.id.fp)
        skip=findViewById(R.id.skip)

        noacc = findViewById(R.id.noacc)
        sp = getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)
        val Isloggedin = sp.getBoolean("Isloggedin", false)
        if (Isloggedin) {
            val i = Intent(this, DashboardActivity::class.java)

            startActivity(i)
            finish()

        }

        login.setOnClickListener {

            val queue = Volley.newRequestQueue(this)
            val url = "https://young-stream-54945.herokuapp.com/login"
            val jsonParams = JSONObject()
            val mobile = etmobile.text.toString()
            val pass = etpass.text.toString()
            jsonParams.put("mobile_number", mobile)
            jsonParams.put("password", pass)
            if (Validations.validateMobile(mobile)) {
                if (Validations.validatePasswordLength(pass)) {
                    if (ConnectionManager().checkconnectivity(this)) {
                        val jsonRequest =
                            object : JsonObjectRequest(
                                Method.POST, url, jsonParams, Response.Listener {
                                    try {
                                        println(it)
                                        val data = it.getJSONObject("data")
                                        val success = data.getBoolean("success")

                                        if (success) {

                                            val data1 = data.getJSONObject("data")
                                            sp.edit()
                                                .putString("user_id", data1.getString("_id"))
                                                .apply()
                                            sp.edit().putString("name", data1.getString("name"))
                                                .apply()
                                            sp.edit().putString("email", data1.getString("email"))
                                                .apply()
                                            sp.edit()
                                                .putString("address", data1.getString("address"))
                                                .apply()
                                            sp.edit().putString(
                                                "mobile_number",
                                                data1.getString("mobile_number")
                                            ).apply()
                                            saveprefrences()
                                            val i = Intent(this, DashboardActivity::class.java)
                                            startActivity(i)
                                            finish()
                                        } else {
                                            Toast.makeText(
                                                this,
                                                "Wrong Login Credentials",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } catch (e: JSONException) {
                                        Toast.makeText(this, "Error1212", Toast.LENGTH_SHORT).show()

                                    }
                                },
                                Response.ErrorListener {
                                    Toast.makeText(
                                        this,
                                        "Volley Error",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            ) {
                                override fun getHeaders(): MutableMap<String, String> {
                                    val headers = HashMap<String, String>()
                                    headers["Content-Type"] = "application/json"
                                    return headers
                                }


                            }
                        queue.add(jsonRequest)
                    } else {
                        val alert = AlertDialog.Builder(this)
                        alert.setTitle("Error")
                        alert.setMessage("INTERNET connection not found")
                        alert.setPositiveButton("open settings") { text, listener ->
                            val i = Intent(Settings.ACTION_WIFI_SETTINGS)
                            startActivity(i)
                            this.finish()


                        }
                        alert.setNegativeButton("exit") { text, listener ->
                            ActivityCompat.finishAffinity(this)

                        }
                        alert.create().show()


                    }
                } else {
                    etpass.error = "Invalid password"

                }
            } else {
                etmobile.error = "Invalid Phone Number"

            }

        }
        skip.setOnClickListener{
            val i = Intent(this, DashboardActivity::class.java)
            startActivity(i)
            finish()
        }



        noacc.setOnClickListener {

            val j = Intent(this, RegisterActivity::class.java)
            startActivity(j)
        }
        fp.setOnClickListener {

            val j = Intent(this,ForgetPassActivity::class.java)
            startActivity(j)
        }
    }

    fun saveprefrences() {
        sp.edit().putBoolean("Isloggedin", true).apply()
    }

    override fun onBackPressed() {
        ActivityCompat.finishAffinity(this)
    }
}