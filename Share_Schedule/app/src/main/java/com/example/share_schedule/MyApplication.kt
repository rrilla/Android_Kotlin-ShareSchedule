package com.example.share_schedule

import android.app.Application
import android.content.SharedPreferences
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MyApplication: Application() {

    companion object {
//        lateinit var viewModelFactory: ViewModelFactory
//        lateinit var dataBase: CalendarDatabase
        lateinit var preferenceManager: PreferenceManager
        lateinit var firebaseAuth: FirebaseAuth
        lateinit var gso: GoogleSignInOptions
        lateinit var gsc: GoogleSignInClient


//        var email: String? = null
//        fun checkAuth(): Boolean {
//            val currentUser = fa.currentUser
//            return currentUser?.let {
//                email = currentUser.email
//                currentUser.isEmailVerified
//            } ?: let {
//                false
//            }
//        }
    }

    override fun onCreate() {
        super.onCreate()

//        dataBase = Room.databaseBuilder(
//            baseContext,
//            CalendarDatabase::class.java,
//            CalendarDatabase.DB_NAME
//        ).build()
        preferenceManager = PreferenceManager(this)
//        viewModelFactory = ViewModelFactory(pm, CalendarRepositoryImpl())

        firebaseAuth = Firebase.auth
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        gsc = GoogleSignIn.getClient(this, gso)
    }
}