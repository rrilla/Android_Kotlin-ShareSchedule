package com.example.share_schedule.signin

import android.accounts.Account
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.share_schedule.MyApplication
import com.example.share_schedule.MyApplication.Companion.firebaseAuth
import com.example.share_schedule.R
import com.example.share_schedule.calendar.CalendarActivity
import com.example.share_schedule.data.remote.GoogleCalendarApiProvider
import com.example.share_schedule.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.calendar.CalendarScopes
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import java.lang.Exception

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()
    lateinit var binding: ActivityLoginBinding

    private val loginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            task.getResult(ApiException::class.java)?.let { account->
                viewModel.saveToken(account.idToken ?: throw Exception())
            } ?: throw Exception()
        } catch (e: ApiException) {
            Log.w("LoginActivity", "signInResult:failed code=" + e.statusCode);
            viewModel.setState(ProfileState.Error)
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkLastLogin()
        observeData()
        initViews()
    }

    private fun checkLastLogin() {
        firebaseAuth.currentUser?.let {
            createGoogleCredential(firebaseAuth.currentUser)
            passToCalendarActivity(autoLogin = true)
        }
    }

    private fun createGoogleCredential(user: FirebaseUser?) {
        // usingOAuth2() - OAuth 2.0 범위를 사용하여 새 인스턴스를 생성.
        val googleCredential = GoogleAccountCredential.usingOAuth2(
            applicationContext, listOf(CalendarScopes.CALENDAR)
        ).setBackOff(ExponentialBackOff())
        // setbackoff - I/O 예외가 #getToken 내부 또는 없음으로 throw될 때 사용되는 역오프 정책을 반환.

        googleCredential.selectedAccount = Account(user?.email, packageName)
        createService(googleCredential)
    }

    private fun createService(credential: GoogleAccountCredential) {
        val transport = GoogleNetHttpTransport.newTrustedTransport()
        val jsonFactory = GsonFactory.getDefaultInstance()

        GoogleCalendarApiProvider.createService(transport, jsonFactory, credential)
    }

    private fun passToCalendarActivity(autoLogin: Boolean) {
        intent = Intent(this@LoginActivity, CalendarActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            putExtra(getString(R.string.autoLogin), autoLogin)
        }
        startActivity(intent)
        finish()
    }

    private fun observeData() = viewModel.profileStateLiveData.observe(this) {
        when (it) {
            is ProfileState.Uninitialized -> initViews()
            is ProfileState.Loading -> handleLoadingState()
            is ProfileState.Login -> handleLoginState(it)
            is ProfileState.Success -> handleSuccessState(it)
            is ProfileState.Error -> handleErrorState()
        }
    }

    private fun initViews() = with(binding) {
        signInButton.setOnClickListener {
            signInGoogle()
        }
    }

    private fun signInGoogle() {
        val signInIntent = MyApplication.gsc.signInIntent
        loginLauncher.launch(signInIntent)
    }

    private fun handleLoadingState() = with(binding) {
        Log.e("LoginActivity", "Loading...")
    }

    private fun handleLoginState(state: ProfileState.Login) = with(binding) {
        val credential = GoogleAuthProvider.getCredential(state.idToken, null)

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this@LoginActivity) { task->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    createGoogleCredential(user)
                    viewModel.setUserInfo(user)
                } else {
                    viewModel.setUserInfo(null)
                }
            }
    }

    private fun handleSuccessState(state: ProfileState.Success) = with(binding) {
        when (state) {
            is ProfileState.Success.Registered -> {
                handleRegisteredState(state)
            }
            is ProfileState.Success.NotRegistered -> {
                handleNotRegisteredState(state)
            }
        }
    }

    private fun handleRegisteredState(state: ProfileState.Success.Registered) {
        passToCalendarActivity(autoLogin = false)
    }

    private fun handleNotRegisteredState(state: ProfileState.Success.NotRegistered) {
        Toast.makeText(this@LoginActivity, R.string.signIn_fail_firebase, Toast.LENGTH_SHORT).show()
    }

    private fun handleErrorState() {
        Toast.makeText(this, R.string.signIn_fail_google, Toast.LENGTH_SHORT).show()
    }
}