package com.gameschat.app.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.*
import android.widget.*
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import com.gameschat.app.R
import com.gameschat.app.ui.ChatActivity
import com.gameschat.app.ui.ChatScreen
import com.gameschat.app.ui.theme.GamesChatAppTheme
import com.gameschat.app.ui.viewmodel.AuthViewModel
import com.gameschat.app.ui.viewmodel.ChatViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class FloatingChatService : Service(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {
    
    private lateinit var windowManager: WindowManager
    private var floatingView: View? = null
    private var isExpanded = false
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val lifecycleRegistry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle get() = lifecycleRegistry
    
    private val store = ViewModelStore()
    override val viewModelStore: ViewModelStore get() = store
    
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    private lateinit var userId: String
    private lateinit var username: String

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        userId = intent?.getStringExtra("user_id") ?: ""
        username = intent?.getStringExtra("username") ?: ""

        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)

        showMinimizedIcon()
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED

        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Floating Chat",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Floating chat overlay notification"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, ChatActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Games Chat")
            .setContentText("Floating chat is active")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun showMinimizedIcon() {
        removeFloatingView()

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = 0
            y = 100
        }

        val iconView = LayoutInflater.from(this).inflate(R.layout.floating_icon, null)
        val chatIcon = iconView.findViewById<ImageView>(R.id.chat_icon)
        val closeButton = iconView.findViewById<ImageView>(R.id.close_button)

        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f

        chatIcon.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (initialTouchX - event.rawX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager.updateViewLayout(iconView, params)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (Math.abs(event.rawX - initialTouchX) < 10 && 
                        Math.abs(event.rawY - initialTouchY) < 10) {
                        showExpandedChat()
                    }
                    true
                }
                else -> false
            }
        }

        closeButton.setOnClickListener {
            stopSelf()
        }

        floatingView = iconView
        windowManager.addView(iconView, params)
        isExpanded = false
    }

    private fun showExpandedChat() {
        removeFloatingView()

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            (windowManager.defaultDisplay.height * 0.7).toInt(),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.BOTTOM
        }

        val expandedView = FrameLayout(this)
        val composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@FloatingChatService)
            setViewTreeViewModelStoreOwner(this@FloatingChatService)
            setViewTreeSavedStateRegistryOwner(this@FloatingChatService)
            
            setContent {
                val authViewModel = remember {
                    AuthViewModel().apply {
                        setCurrentUser(com.gameschat.app.data.model.User(
                            id = userId,
                            username = username,
                            passwordHash = ""
                        ))
                    }
                }
                val chatViewModel: ChatViewModel = viewModel()

                GamesChatAppTheme {
                    ChatScreen(
                        chatViewModel = chatViewModel,
                        authViewModel = authViewModel,
                        onLogout = { stopSelf() },
                        onEnableFloating = { showMinimizedIcon() }
                    )
                }
            }
        }

        expandedView.addView(composeView)

        floatingView = expandedView
        windowManager.addView(expandedView, params)
        isExpanded = true
    }

    private fun removeFloatingView() {
        floatingView?.let {
            try {
                windowManager.removeView(it)
            } catch (e: Exception) {
            }
            floatingView = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeFloatingView()
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        serviceScope.cancel()
        store.clear()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val CHANNEL_ID = "floating_chat_channel"
        private const val NOTIFICATION_ID = 1
    }
}
