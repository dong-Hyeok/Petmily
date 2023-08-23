package com.petmily.presentation.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.petmily.R
import com.petmily.config.ApplicationClass
import com.petmily.config.BaseActivity
import com.petmily.databinding.ActivityMainBinding
import com.petmily.presentation.view.board.BoardDetailFragment
import com.petmily.presentation.view.board.BoardWriteFragment
import com.petmily.presentation.view.certification.join.JoinFragment
import com.petmily.presentation.view.certification.login.LoginFragment
import com.petmily.presentation.view.certification.password.PasswordFragment
import com.petmily.presentation.view.chat.ChatDetailFragment
import com.petmily.presentation.view.chat.ChatUserListFragment
import com.petmily.presentation.view.curation.CurationDetailFragment
import com.petmily.presentation.view.curation.CurationMainFragment
import com.petmily.presentation.view.curation.WebViewFragment
import com.petmily.presentation.view.gallery.GalleryFragment
import com.petmily.presentation.view.home.HomeFragment
import com.petmily.presentation.view.info.pet.PetInfoFragment
import com.petmily.presentation.view.info.pet.PetInfoInputFragment
import com.petmily.presentation.view.info.user.UserInfoInputFragment
import com.petmily.presentation.view.mypage.MyPageFragment
import com.petmily.presentation.view.notification.NotificationFragment
import com.petmily.presentation.view.search.SearchFragment
import com.petmily.presentation.view.splash.SplashFragment
import com.petmily.presentation.view.store.PointLogFragment
import com.petmily.presentation.view.store.ShopFragment
import com.petmily.presentation.view.walk.WalkFragment
import com.petmily.presentation.viewmodel.BoardViewModel
import com.petmily.presentation.viewmodel.CurationViewModel
import com.petmily.presentation.viewmodel.MainViewModel
import com.petmily.presentation.viewmodel.UserViewModel
import com.petmily.repository.dto.Board
import com.petmily.repository.dto.UserLoginInfoDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "petmily_MainActivity"
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    lateinit var bottomNavigationView: BottomNavigationView

    private val mainViewModel: MainViewModel by viewModels()
    private val curationViewModel: CurationViewModel by viewModels()
    private val boardViewModel: BoardViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 토큰 수신
        getToken()

        initObserver()
        bottomNavigationView = binding.bottomNavigation

        changeFragment("splash")
        CoroutineScope(Dispatchers.Main).launch {
            delay(1200)
            initCheckCertification()
        }

        initBottomNavigation()
        initFragmentAnimation()
    }

    private fun initCheckCertification() {
        Log.d(TAG, "onCreate: ${ApplicationClass.sharedPreferences.getString("userEmail")} / ${ApplicationClass.sharedPreferences.getString("userNickname")}")
        ApplicationClass.sharedPreferences.apply {
            if (!getString("userEmail").isNullOrBlank() && !getString("userNickname").isNullOrBlank()) { // 로그인 성공 & 닉네임 보유
                // 홈 화면 데이터 로드
                initSetting()
                // 로그인 성공시 FCM 토큰 전송
                mainViewModel.requstSaveToken()
            } else if (!getString("userEmail").isNullOrBlank() && getString("userNickname").isNullOrBlank()) { // 로그인 성공 & 닉네임 미보유
                // 로그인 성공시 FCM 토큰 전송
                mainViewModel.requstSaveToken()
                changeFragment("userInfoInput")
            } else { // 로그인 실패
                changeFragment("login")
            }
        }
    }

    /**
     * 최초 Data 세팅
     */
    fun initSetting() {
        curationViewModel.requestCurationData("all") // curation Data 요청
        userViewModel.requestMypageInfo() // myPage User info 요청
    }

    private fun initObserver() {
        // 큐레이션 초기 데이터 GET
        curationViewModel.curationAllList.observe(this) {
            Log.d(TAG, "initObserver: curationAllList $it")
            Log.d(TAG, "initObserver: curationAllList ${curationViewModel.curationDogList.value}")
            curationViewModel.getRandomCurationList()
        }

        curationViewModel.randomCurationList.observe(this) {
            Log.d(TAG, "initObserver: randomCurationList $it")
            changeFragment("home")
            bottomNavigationView.visibility = View.VISIBLE
        }

        // 액세스 토큰 재발급
        mainViewModel.newAccessToken.observe(this) {
            if (it.isNullOrBlank()) {
                // 토큰 재발급 실패
                showSnackbar("로그인이 만료되었습니다. 다시 로그인해주세요.")

                ApplicationClass.sharedPreferences.removeUser()
                changeFragment("login")
            } else {
                // 토큰 재발급 성공
                ApplicationClass.sharedPreferences.addAccessToken(it)
            }
        }
    }

    /**
     * Fragment 애니메이션 전환
     */
    private fun initFragmentAnimation(): FragmentTransaction {
        return supportFragmentManager.beginTransaction().setCustomAnimations(
            R.anim.fragment_to_right,
            R.anim.fragment_from_right,
            R.anim.fragment_to_left,
            R.anim.fragment_from_left,
        )
    }

    private fun initBottomNavigation() {
        bottomNavigationView.apply {
            setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_page_home -> {
                        changeFragment("home")
                    }

                    R.id.navigation_page_curation -> {
                        changeFragment("curation")
                    }

                    R.id.navigation_page_feed_add -> {
                        // 수정 및 삭제용 Board 초기화
                        boardViewModel.selectedBoard = Board()
                        changeFragment("feed add")
                    }

                    R.id.navigation_page_chatting -> {
                        changeFragment("chatting")
                    }

                    R.id.navigation_page_my_page -> {
                        userViewModel.selectedUserLoginInfoDto = UserLoginInfoDto(
                            userEmail = ApplicationClass.sharedPreferences.getString("userEmail") ?: "",
                        )
                        changeFragment("my page")
                    }
                }
                true
            }
        }
    }

    fun bottomNaviVisible() {
        bottomNavigationView.visibility = View.VISIBLE
    }

    fun bottomNaviInVisible() {
        bottomNavigationView.visibility = View.GONE
    }

    fun changeFragment(str: String) {
        when (str) {
            "splash" -> {
                supportFragmentManager.commit {
                    replace(R.id.frame_layout_main, SplashFragment())
                }
            }

            "login" -> {
                bottomNaviInVisible()
                supportFragmentManager.commit {
                    replace(R.id.frame_layout_main, LoginFragment())
                }
            }

            "password" -> {
                supportFragmentManager.commit {
                    addToBackStack("login")
                    replace(R.id.frame_layout_main, PasswordFragment())
                }
            }

            "join" -> {
                supportFragmentManager.commit {
                    addToBackStack("login")
                    replace(R.id.frame_layout_main, JoinFragment())
                }
            }

            "home" -> {
                supportFragmentManager.commit {
                    replace(R.id.frame_layout_main, HomeFragment())
                }
            }

            "curation" -> {
                supportFragmentManager.commit {
                    replace(R.id.frame_layout_main, CurationMainFragment())
                }
            }

            "feed add" -> {
                supportFragmentManager.commit {
                    if (boardViewModel.selectedBoard.boardId != 0L) {
                        addToBackStack("feed add")
                    }
                    mainViewModel.initAddPhotoList()
                    replace(R.id.frame_layout_main, BoardWriteFragment())
                }
            }

            "chatting" -> {
                supportFragmentManager.commit {
                    replace(R.id.frame_layout_main, ChatUserListFragment())
                }
            }

            "my page" -> {
                supportFragmentManager.commit {
                    replace(R.id.frame_layout_main, MyPageFragment())
                }
            }

            "userInfoInput" -> {
                supportFragmentManager.commit {
                    addToBackStack("userInfoInput")
                    replace(R.id.frame_layout_main, UserInfoInputFragment())
                }
            }

            "petInfoInput" -> {
                supportFragmentManager.commit {
                    addToBackStack("petInfoInput")
                    replace(R.id.frame_layout_main, PetInfoInputFragment())
                }
            }

            "petInfo" -> {
                supportFragmentManager.commit {
                    addToBackStack("petInfo")
                    replace(R.id.frame_layout_main, PetInfoFragment())
                }
            }

            "curation detail" -> {
                initFragmentAnimation().apply {
                    addToBackStack("curation detail")
                    replace(R.id.frame_layout_main, CurationDetailFragment())
                }.commit()
            }

            "gallery" -> {
                supportFragmentManager.commit {
                    addToBackStack("gallery")
                    replace(R.id.frame_layout_main, GalleryFragment())
                }
            }

            "search" -> {
                supportFragmentManager.commit {
                    addToBackStack("search")
                    replace(R.id.frame_layout_main, SearchFragment())
                }
            }

            "chat detail" -> {
                supportFragmentManager.commit {
                    addToBackStack("chat detail")
                    replace(R.id.frame_layout_main, ChatDetailFragment())
                }
            }

            "notification" -> {
                supportFragmentManager.commit {
                    addToBackStack("notification")
                    replace(R.id.frame_layout_main, NotificationFragment())
                }
            }

            "board detail" -> {
                supportFragmentManager.commit {
                    addToBackStack("board detail")
                    replace(R.id.frame_layout_main, BoardDetailFragment())
                }
            }

            "webView" -> {
                supportFragmentManager.commit {
                    addToBackStack("webView")
                    replace(R.id.frame_layout_main, WebViewFragment())
                }
            }

            "pointLog" -> {
                supportFragmentManager.commit {
                    addToBackStack("pointLog")
                    replace(R.id.frame_layout_main, PointLogFragment())
                }
            }

            "shop" -> {
                supportFragmentManager.commit {
                    addToBackStack("shop")
                    replace(R.id.frame_layout_main, ShopFragment())
                }
            }

            "walk" -> {
                supportFragmentManager.commit {
                    addToBackStack("walk")
                    replace(R.id.frame_layout_main, WalkFragment())
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(
            OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                Log.d(TAG, "onCreate onNewToken: $token")

                mainViewModel.uploadFcmToken(token)
            },
        )
        createNotificationChannel(channel_id, "ssafy")
    }

    // Notification 수신을 위한 채널 추가
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(id: String, name: String) {
        val importance = NotificationManager.IMPORTANCE_HIGH // 알림 소리
        val channel = NotificationChannel(id, name, importance)

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        // FCM 채널
        const val channel_id = "fcm_channel"
    }
}
