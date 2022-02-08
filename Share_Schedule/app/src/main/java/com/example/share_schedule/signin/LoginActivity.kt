package com.example.share_schedule.signin

import android.accounts.Account
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.jh_calendar.signin.ProfileState
import com.example.share_schedule.MyApplication
import com.example.share_schedule.R
import com.example.share_schedule.calendar.CalendarActivity
import com.example.share_schedule.data.remote.GoogleCalendarApiProvider
import com.example.share_schedule.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.calendar.CalendarScopes
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import java.lang.Exception

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()
    lateinit var binding: ActivityLoginBinding
    private fun getViewBinding(): ActivityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val loginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            task.getResult(ApiException::class.java)?.let { account->
                viewModel.saveToken(account.idToken ?: throw Exception())
            } ?: throw Exception()
        } catch (e: ApiException) {
            Log.w("LoginActivity", "signInResult:failed code=" + e.statusCode);
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
        setContentView(binding.root)

        checkLastLogin()
        observeData()
        initViews()
    }

    private fun checkLastLogin() {
        if(firebaseAuth.currentUser != null) {
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

        googleCredential.selectedAccount = Account(user?.email,packageName)
        createService(googleCredential)
    }

    private fun createService(credential: GoogleAccountCredential) {
        val transport = GoogleNetHttpTransport.newTrustedTransport()
        val jsonFactory = GsonFactory.getDefaultInstance()

        GoogleCalendarApiProvider.createService(transport, jsonFactory, credential)
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
                    Toast.makeText(this@LoginActivity, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun handleSuccessState(state: ProfileState.Success) = with(binding) {
        when (state) {
            is ProfileState.Success.Registered -> {
                handleRegisteredState(state)
            }
            is ProfileState.Success.NotRegistered -> {
            }
        }
    }

    private fun handleRegisteredState(state: ProfileState.Success.Registered) {
        passToCalendarActivity(autoLogin = false)
    }

    private fun passToCalendarActivity(autoLogin: Boolean) {
        intent = Intent(this@LoginActivity, CalendarActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            putExtra(getString(R.string.autoLogin), autoLogin)
        }
        startActivity(intent)
        finish()
    }

    private fun handleErrorState() {
        Toast.makeText(this, "오류가 발생했습니다. 잠시 후 다시 시도해주세요", Toast.LENGTH_SHORT).show()
    }
}