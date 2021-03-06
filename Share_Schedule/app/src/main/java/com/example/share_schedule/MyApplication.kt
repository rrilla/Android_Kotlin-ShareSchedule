package com.example.share_schedule

import android.app.Application
import androidx.room.Room
import com.example.share_schedule.data.db.CalendarDatabase
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.common.KakaoSdk

class MyApplication: Application() {

    companion object {
        lateinit var preferenceManager: PreferenceManager
        lateinit var firebaseAuth: FirebaseAuth
        lateinit var gso: GoogleSignInOptions
        lateinit var gsc: GoogleSignInClient
        lateinit var dataBase: CalendarDatabase

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

        preferenceManager = PreferenceManager(this)

        firebaseAuth = Firebase.auth
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.DEFAULT_WEB_CLIENT_ID)
            .requestEmail()
            .build()

        gsc = GoogleSignIn.getClient(this, gso)

        dataBase = Room.databaseBuilder(
            baseContext,
            CalendarDatabase::class.java,
            CalendarDatabase.DB_NAME
        ).build()

        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
    }
}