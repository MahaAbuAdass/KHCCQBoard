package com.example.khccqboard.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.khccqboard.R
import com.example.khccqboard.data.CurrentQ
import com.example.khccqboard.data.FileURL
import com.example.khccqboard.data.TimeResponse

import com.example.khccqboard.databinding.DetailsFragmentBinding
import com.example.khccqboard.util.PreferenceManager
import com.example.khccqboard.viewmodel.CurrentTicketViewModel
import com.example.khccqboard.viewmodel.CurrentTimeViewModel
import com.example.khccqboard.viewmodel.ScrollMessagesViewModel
import com.example.khccqboard.viewmodel.GenericViewModelFactory
import com.example.khccqboard.viewmodel.GetCurrentQViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailsFragment : Fragment() {

    private lateinit var binding: DetailsFragmentBinding
    private lateinit var scrollMessagesViewModel: ScrollMessagesViewModel
    private lateinit var currentQViewModel: GetCurrentQViewModel
    private lateinit var currentTicketViewModel: CurrentTicketViewModel

    // private lateinit var getImagesViewModel: GetImagesViewModel
    private lateinit var getCurrentTimeViewModel: CurrentTimeViewModel
    //  private lateinit var imagesAndVideosViewModel: ImagesAndVideosViewModel

    private var language: String? = null
    private lateinit var viewPager: ViewPager2

    private lateinit var englishTextView: TextView
    private lateinit var arabicTextView: TextView

    var currentQAdapter: TicketAdapter? = null
    var branchCode: String? = null

    private var mediaPlayer: MediaPlayer? = null // Declare a MediaPlayer variable
    private val audioQueue: MutableList<Int> = mutableListOf()

    private val englishAudioQueue: MutableList<Int> = mutableListOf()

    private val handler = Handler()
    private val refreshInterval = 5000L // 5 seconds

    private val timeRefreshHandler = Handler()
    private val timeRefreshInterval = 60000L // 1 minute (60,000 milliseconds)

    private val scrollMsgsHandler = Handler()
    private val scrollMsgsRefreshInterval = 600000L // 10 minutes


    private val timeRefreshRunnable = object : Runnable {
        override fun run() {
            callCurrentTimeApi() // Call the current time API every 1 minute
            timeRefreshHandler.postDelayed(this, timeRefreshInterval)
        }
    }

    private val runnable = object : Runnable {
        override fun run() {
            callGetCurrentQApi()
            callCurrentTicketApi() // Call the API to refresh the data
            handler.postDelayed(this, refreshInterval) // Schedule next execution
            //   screenHandler.postDelayed(this, screenRefreshInterval) // Schedule next execution

        }
    }

   private val scrollMsgsRunnable = object : Runnable {
        override fun run() {
            callGetScrollMsgsApi() // Call the API every 10 minutes
            scrollMsgsHandler.postDelayed(
                this,
                scrollMsgsRefreshInterval
            ) // Schedule next execution
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DetailsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        englishTextView = binding.englishText
        arabicTextView = binding.arabicText

        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        branchCode = PreferenceManager.getBranchCode(requireContext())
        //    displayNumber = PreferenceManager.getDisplayNumber(requireContext())

        val getTimeFactory = GenericViewModelFactory(CurrentTimeViewModel::class) {
            CurrentTimeViewModel(requireContext())
        }
        getCurrentTimeViewModel =
            ViewModelProvider(this, getTimeFactory).get(CurrentTimeViewModel::class.java)

        val factory = GenericViewModelFactory(ScrollMessagesViewModel::class) {
            ScrollMessagesViewModel(requireContext())
        }
        scrollMessagesViewModel =
            ViewModelProvider(this, factory).get(ScrollMessagesViewModel::class.java)

        val currentQFactory = GenericViewModelFactory(GetCurrentQViewModel::class) {
            GetCurrentQViewModel(requireContext())
        }
        currentQViewModel =
            ViewModelProvider(this, currentQFactory).get(GetCurrentQViewModel::class.java)


        val currentTicketFactory = GenericViewModelFactory(CurrentTicketViewModel::class) {
            CurrentTicketViewModel(requireContext())
        }
        currentTicketViewModel =
            ViewModelProvider(this, currentTicketFactory).get(CurrentTicketViewModel::class.java)

//        val getImagesFactory = GenericViewModelFactory(GetImagesViewModel::class) {
//            GetImagesViewModel(requireContext())
//        }
//        getImagesViewModel =
//            ViewModelProvider(this, getImagesFactory).get(GetImagesViewModel::class.java)


//        val getFilesFactory = GenericViewModelFactory(ImagesAndVideosViewModel::class) {
//            ImagesAndVideosViewModel(requireContext())
//        }
//        imagesAndVideosViewModel =
//            ViewModelProvider(this, getFilesFactory).get(ImagesAndVideosViewModel::class.java)


//        callGetImagesApi()
//        observerGetImagesViewModel()
        callCurrentTicketApi()
        observerCurrentTicketViewModel()
        callGetCurrentQApi()
        observerCallCurrentQApi()
        callGetScrollMsgsApi()
        observerScrollMsgsViewModel()
        callCurrentTimeApi()
        observerCurrentTimeViewModel()
//        callGetImagesAndVideosApi()
//        observerImagesAndVideosViewModel()

        setupMarquee(arabicTextView, true, 55000L)   // Arabic text (right to left)
        setupMarquee(englishTextView, false, 45000L)


        binding.logo.setOnClickListener {
            PreferenceManager.clearUrl(requireContext())
            PreferenceManager.setURl(requireContext(), false)
            findNavController().navigate(DetailsFragmentDirections.actionDetailsFragmentToHomeFragment())
        }




        binding.tvTime.setOnClickListener {
            PreferenceManager.clearUrl(requireContext())
            PreferenceManager.setURl(requireContext(), false)
            findNavController().navigate(DetailsFragmentDirections.actionDetailsFragmentToHomeFragment())
        }

        handler.post(runnable)
        startMarqueeApiCall()

    }

    private fun observerCallCurrentQApi() {
        currentQViewModel.getCurrentQResponse.observe((viewLifecycleOwner)) {currentQList->
//            val currentQList = listOf(
//                CurrentQ(TicketNo = "123", CounterId = 1),
//                CurrentQ(TicketNo = "124", CounterId = 2),
//                CurrentQ(TicketNo = "125", CounterId = 3)
//            )

            currentQAdapter(currentQList)

        }

        currentQViewModel.errorResponse.observe(viewLifecycleOwner) {
            // Toast.makeText(requireContext(), "get currentQ List Api failed", Toast.LENGTH_SHORT).show()
        }

    }

    private fun callGetCurrentQApi() {
        CoroutineScope(Dispatchers.IO).launch {
            currentQViewModel.getCurrentQ(branchCode ?: "")
        }
    }

    fun currentQAdapter(currentQList: List<CurrentQ?>) {

        currentQAdapter = TicketAdapter(currentQList)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = currentQAdapter

    }


    private fun startMarqueeApiCall() {
        scrollMsgsHandler.post(scrollMsgsRunnable) // Start the marquee API refresh loop
    }

    private fun callGetScrollMsgsApi() {
        CoroutineScope(Dispatchers.IO).launch {
            scrollMessagesViewModel.getScrollMsgs(branchCode ?: "")

        }

    }

    override fun onStart() {
        super.onStart()
        handler.post(runnable) // Start the auto-refresh when fragment is visible
        timeRefreshHandler.postDelayed(
            timeRefreshRunnable,
            timeRefreshInterval
        ) // Start time refresh
        //      screenHandler.post(runnable) // Start the auto-refresh when fragment is visible

        scrollMsgsHandler.post(scrollMsgsRunnable) // Start the scroll messages refresh every 10 minutes
    }

    private fun observerScrollMsgsViewModel() {
        scrollMessagesViewModel.getMsgsResponse.observe(viewLifecycleOwner) { scrollMsgs ->
            //    binding.arabicText.text= "اهلا وسهلا بكم في المركز"
            binding.arabicText.text = scrollMsgs?.ScrollMessageAr
            binding.englishText.text = scrollMsgs?.ScrollMessageEn

            setupMarquee(arabicTextView, true, 55000L)
            setupMarquee(englishTextView, false, 45000L)
        }

        scrollMessagesViewModel.errorResponse.observe(viewLifecycleOwner) {
            Toast.makeText(
                requireContext(),
                "please check your network or the entered info \"Base Url or branch code\"",
                Toast.LENGTH_SHORT
            )
                .show()
        }

    }

    private fun setupMarquee(textView: TextView, isArabic: Boolean, durationMarquee: Long) {
        textView.post {
            val textWidth = textView.paint.measureText(textView.text.toString())
            val viewWidth = textView.width

            Log.d("MarqueeSetup", "Text Width: $textWidth, View Width: $viewWidth")

            // Adjust the start and end positions based on the language
            val startX =
                if (isArabic) -textWidth else viewWidth.toFloat() // Arabic starts from left (-textWidth)
            val endX =
                if (isArabic) viewWidth.toFloat() else -textWidth // English starts from right (viewWidth)

            val animator = ObjectAnimator.ofFloat(textView, "translationX", startX, endX).apply {
                duration = durationMarquee // Adjust duration for slower animation
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.RESTART
                interpolator = LinearInterpolator()

                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationRepeat(animation: Animator) {
                        // Reset translation based on the direction
                        textView.translationX = if (isArabic) -textWidth else viewWidth.toFloat()
                    }
                })
            }

            animator.start()
        }
    }

    private fun callCurrentTicketApi() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            currentTicketViewModel.getCurrentTicket(
                branchCode ?: ""
                //    ,displayNumber?:""
            )
        }
    }

    private fun observerCurrentTicketViewModel() {
        currentTicketViewModel.currentTicket.observe(viewLifecycleOwner) { currentTicket ->

            val ticketNumberAudio = currentTicket.TicketNo
            val counterIdAudio = currentTicket.CounterId?.toInt()

            when (language) {
                "1" -> {
                    // Play only English audio
                    //  playEnglishAudio(ticketNumberAudio, counterIdAudio)
                }

                "2" -> {
                    // Play only Arabic audio
                    //playArabicAudio(ticketNumberAudio, counterIdAudio)
                }

                "3" -> {
                    // Play both English and Arabic audios sequentially
                    //playEnglishAudio(ticketNumberAudio, counterIdAudio) {
                    // Play Arabic after English finishes
                    //  playArabicAudio(ticketNumberAudio, counterIdAudio)
                }
            }

            //  else -> {
            Log.e("AudioError", "Invalid language value: $language")
            //   }
        }


//            binding.titleCurrent.text = "${currentTicket.TicketNo}"
//            flashText(binding.titleCurrent, "${currentTicket.TicketNo} ")
//
//            binding.noCurrent.text = "Counter: ${currentTicket.CounterId}"
//
//            // Log the image path
//            Log.d("ImagePath", currentTicket.Path ?: "")


        //   binding.imgCurrent.setImageResource(R.drawable.placeholder) // Set placeholder

//            Glide.with(this)
//                .load(currentTicket.Path)
//                .skipMemoryCache(true) // Skip memory caching
//                .diskCacheStrategy(DiskCacheStrategy.NONE) // Skip disk caching
//                .signature(ObjectKey(System.currentTimeMillis().toString())) // Force reload
//                .listener(object : RequestListener<Drawable> {
//                    override fun onLoadFailed(
//                        e: GlideException?,
//                        model: Any?,
//                        target: Target<Drawable>?,
//                        isFirstResource: Boolean
//                    ): Boolean {
//                        Log.e("GlideError", "Failed to load image", e)
//                        return false // Return false to let Glide handle the error
//                    }
//
//                    override fun onResourceReady(
//                        resource: Drawable?,
//                        model: Any?,
//                        target: Target<Drawable>?,
//                        dataSource: DataSource,
//                        isFirstResource: Boolean
//                    ): Boolean {
//                        Log.d("GlideSuccess", "Image loaded successfully")
//                        return false // Return false to let Glide continue handling the resource
//                    }
//                })
//                .into(binding.imgCurrent)

    }


    private fun flashText(textView: TextView, text: String) {
        val handler = Handler(Looper.getMainLooper())
        textView.text = text
        val duration = 500L // Duration for each fade in/out
        val totalDuration = 5000L // Total duration for flashing

        // Start the flashing animation
        val runnable = object : Runnable {
            var elapsedTime = 0L
            override fun run() {
                if (elapsedTime < totalDuration) { // Run for 5 seconds (5000 ms)
                    // Create an animation to fade out or in
                    val alphaAnimation = ObjectAnimator.ofFloat(
                        textView,
                        "alpha",
                        if (textView.alpha == 1f) 0f else 1f
                    )
                    alphaAnimation.duration = duration
                    alphaAnimation.start()

                    elapsedTime += duration // Increment elapsed time
                    handler.postDelayed(this, duration) // Repeat after specified duration
                } else {
                    // Ensure the text is fully visible at the end of 5 seconds
                    textView.alpha = 1f
                }
            }
        }

        handler.post(runnable)
    }

    private fun callCurrentTimeApi() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            getCurrentTimeViewModel.getCurrentTime()
        }
    }


//    private fun playEnglishAudio(
//        ticketNumberAudio: String?,
//        counterIdAudio: Int?,
//        onFinish: (() -> Unit)? = null
//    ) {
//        val firstCharEn = ticketNumberAudio?.firstOrNull()?.lowercaseChar()?.toString()
//        val ticketNumberWithoutPrefixEn = ticketNumberAudio?.substring(1)?.toInt()
//        Log.v("ticket number", firstCharEn + ticketNumberWithoutPrefixEn)
//
//
//
//        englishAudioQueue.clear()
//        englishAudioQueue.add(R.raw.doorbell)
////        englishAudioQueue.add(R.raw.enticketnumber)
//
//        englishAudioQueue.add(R.raw.enticketnumber)
//        // ticket character
//        val ticketId = resources.getIdentifier("en$firstCharEn", "raw", requireContext().packageName)
//        englishAudioQueue.add(ticketId)
//
//
//        // add ticket number
//        if (ticketNumberWithoutPrefixEn in 1..19) {
//            // Play the single audio file for numbers 1-19
//            val englishTicketAudioFileName = "en$ticketNumberWithoutPrefixEn"
//
//            val resourceId = resources.getIdentifier(
//                englishTicketAudioFileName,
//                "raw",
//                requireContext().packageName
//            )
//            Log.d(
//                "AudioResource",
//                "Resource ID for $englishTicketAudioFileName: $resourceId : $ticketNumberWithoutPrefixEn"
//            )
//
//            if (resourceId != 0) {
//                englishAudioQueue.add(resourceId)
//            } else {
//                Log.e("AudioError", "Resource not found for: $englishTicketAudioFileName")
//            }
//
//        } else if (ticketNumberWithoutPrefixEn in 20..99) {
//            // Split the number into tens and ones digits
//            val tens = ticketNumberWithoutPrefixEn?.div(10)  // e.g., 7 for 76
//            val ones = ticketNumberWithoutPrefixEn?.rem(10)  // e.g., 6 for 76
//
//            // Play the first audio for the tens digit (e.g., "en_70")
//            val englishTensAudioFileName = "en${tens?.times(10)}" // e.g., "en70"
//            val tensResourceId = resources.getIdentifier(
//                englishTensAudioFileName,
//                "raw",
//                requireContext().packageName
//            )
//
//            if (tensResourceId != 0) {
//                englishAudioQueue.add(tensResourceId)
//            } else {
//                println("English audio file not found for tens: $tens")
//            }
//
//            // Play the second audio for the ones digit if it's not 0 (e.g., "en_6")
//            if (ones != 0) {
//                val englishOnesAudioFileName = "en$ones" // e.g., "en_6"
//                val onesResourceId = resources.getIdentifier(
//                    englishOnesAudioFileName,
//                    "raw",
//                    requireContext().packageName
//                )
//
//                if (onesResourceId != 0) {
//                    englishAudioQueue.add(onesResourceId)
//                } else {
//                    println("English audio file not found for ones: $ones")
//                }
//            }
//        } else if (ticketNumberWithoutPrefixEn in 100..999) {
//            // Split the number into hundreds, tens, and ones digits
//            val hundreds = ticketNumberWithoutPrefixEn?.div(100)  // e.g., 7 for 736
//            val tens = (ticketNumberWithoutPrefixEn?.rem(100))?.div(10)  // e.g., 3 for 736
//            val ones = ticketNumberWithoutPrefixEn?.rem(10)  // e.g., 6 for 736
//
//            // Play the audio for the hundreds digit (e.g., "en_700")
//            val englishHundredsAudioFileName = "en${hundreds?.times(100)}" // e.g., "en_700"
//            val hundredsResourceId = resources.getIdentifier(
//                englishHundredsAudioFileName,
//                "raw",
//                requireContext().packageName
//            )
//
//            if (hundredsResourceId != 0) {
//                englishAudioQueue.add(hundredsResourceId)
//            } else {
//                Log.e("AudioError", "English audio file not found for hundreds: $hundreds")
//            }
//
//            // Play the audio for the tens digit if it's not 0 (e.g., "en_30")
//            if (tens != 0) {
//                val englishTensAudioFileName = "en${tens?.times(10)}" // e.g., "en_30"
//                val tensResourceId = resources.getIdentifier(
//                    englishTensAudioFileName,
//                    "raw",
//                    requireContext().packageName
//                )
//
//                if (tensResourceId != 0) {
//                    englishAudioQueue.add(tensResourceId)
//                } else {
//                    Log.e("AudioError", "English audio file not found for tens: $tens")
//                }
//            }
//
//            // Play the audio for the ones digit if it's not 0 (e.g., "en_6")
//            if (ones != 0) {
//                val englishOnesAudioFileName = "en$ones" // e.g., "en_6"
//                val onesResourceId = resources.getIdentifier(
//                    englishOnesAudioFileName,
//                    "raw",
//                    requireContext().packageName
//                )
//
//                if (onesResourceId != 0) {
//                    englishAudioQueue.add(onesResourceId)
//                } else {
//                    Log.e("AudioError", "English audio file not found for ones: $ones")
//                }
//            }
//        }
//
////        englishAudioQueue.add(R.raw.please) // Assuming audio2.mp3 is in res/raw
////        englishAudioQueue.add(R.raw.go) // Assuming audio2.mp3 is in res/raw
////        englishAudioQueue.add(R.raw.counter) // Assuming audio2.mp3 is in res/raw
//        englishAudioQueue.add(R.raw.enpleasegotocounter)
//
//
//        if (counterIdAudio != null) {
//            if (counterIdAudio < 100) {
//                val counterAudioFileName = "en$counterIdAudio"
//                val counterResourceId = resources.getIdentifier(
//                    counterAudioFileName,
//                    "raw",
//                    requireContext().packageName
//                )
//                englishAudioQueue.add(counterResourceId)
//            }
//
//        }
//
//        if (englishAudioQueue.isNotEmpty()) {
//            playNextAudioEnglish {
//                // This will trigger Arabic audio playback when English audio is finished
//                //  playArabicAudio(ticketNumberAudio, counterIdAudio)
//                onFinish?.invoke()
//
//            }
//        } else {
//            // If no English audio is played, directly invoke onFinish
//            onFinish?.invoke()
//        }
//    }
//
//
//    @SuppressLint("DiscouragedApi")
//    private fun playArabicAudio(ticketNumberAudio: String?, counterIdAudio: Int?) {
//        // Remove the first character (the "T") and get the number part
//        val firstChar = ticketNumberAudio?.firstOrNull()?.lowercaseChar()?.toString()
//        val ticketNumberWithoutPrefix = ticketNumberAudio?.substring(1)?.toInt()
//
//        audioQueue.clear()
//        audioQueue.add(R.raw.doorbell)
//        audioQueue.add(R.raw.ticketar)
//        // audioQueue.add(R.raw.artkt)
//
//        // ticket character
//        val ticketId = resources.getIdentifier(firstChar, "raw", requireContext().packageName)
//        audioQueue.add(ticketId)
//
//
//        // add ticket number
//        if (ticketNumberWithoutPrefix != null) {
//            if (ticketNumberWithoutPrefix < 100) {
//                val ticketAudioFileName = "ar$ticketNumberWithoutPrefix" // e.g., "ar_76"
//
//                // Get the resource ID of the dynamically named file (e.g., ar_76.mp3)
//                val resourceId = resources.getIdentifier(
//                    ticketAudioFileName,
//                    "raw",
//                    requireContext().packageName
//                )
//
//                Log.d("AudioResource", "Resource ID for $ticketAudioFileName: $resourceId")
//
//                // Check if the resource exists before adding to the queue
//                if (resourceId != 0) {
//                    audioQueue.add(resourceId)
//                } else {
//                    // Handle case where the sound file doesn't exist
//                    println("Audio file not found for ticket number: $ticketNumberWithoutPrefix")
//                }
//            } else if (ticketNumberWithoutPrefix in 100..999) {
//                // Split the number into hundreds, tens, and ones digits
//                val hundreds = ticketNumberWithoutPrefix.div(100) // e.g., 4 for 406
//                val tensAndOnes = ticketNumberWithoutPrefix.rem(100) // e.g., 34 for 434
//                val tens = (ticketNumberWithoutPrefix.rem(100)).div(10) // e.g., 0 for 406
//                val ones = ticketNumberWithoutPrefix.rem(10) // e.g., 6 for 406
//
//                // Play audio file for hundreds if both tens and ones are zero
//                if (tens == 0 && ones == 0) {
//                    val arabicHundredsAudioFileName = "ar${hundreds.times(100)}" // e.g., "ar400"
//                    val hundredsResourceId = resources.getIdentifier(
//                        arabicHundredsAudioFileName,
//                        "raw",
//                        requireContext().packageName
//                    )
//
//                    if (hundredsResourceId != 0) {
//                        audioQueue.add(hundredsResourceId) // Add hundreds audio
//                    } else {
//                        println("Arabic audio file not found for exact hundreds: $hundreds")
//                    }
//                } else {
//                    val arabicHundredsAudioFileName =
//                        "ar${hundreds?.times(100)?.plus(1)}" // e.g., "ar401"
//
//                    val hundredsResourceId = resources.getIdentifier(
//                        arabicHundredsAudioFileName,
//                        "raw",
//                        requireContext().packageName
//                    )
//
//                    if (hundredsResourceId != 0) {
//                        audioQueue.add(hundredsResourceId) // Add hundreds audio
//                        println("Arabic audio file for exact hundreds: $hundreds")
//
//                    } else {
//                        println("Arabic audio file not found for exact hundreds: $arabicHundredsAudioFileName")
//                    }
//
//
//                    val tensAndOnesFileName = "ar$tensAndOnes" // e.g., "ar_76"
//
//                    // Get the resource ID of the dynamically named file (e.g., ar_76.mp3)
//                    val resourceId = resources.getIdentifier(
//                        tensAndOnesFileName,
//                        "raw",
//                        requireContext().packageName
//                    )
//
//                    Log.d("AudioResource", "Resource ID for $tensAndOnesFileName: $resourceId")
//
//                    // Check if the resource exists before adding to the queue
//                    if (resourceId != 0) {
//                        audioQueue.add(resourceId)
//                    } else {
//                        // Handle case where the sound file doesn't exist
//                        println("Audio file not found for ticket number: $ticketNumberWithoutPrefix")
//                    }
//                }
//
//            }
//        }
//        // audioQueue.add(R.raw.collectionarea)
//
//        audioQueue.add(R.raw.arabic)
//
//        if (counterIdAudio != null) {
//            if (counterIdAudio < 100) {
//                val counterAudioFileName = "ar$counterIdAudio"
//                val counterResourceId = resources.getIdentifier(
//                    counterAudioFileName,
//                    "raw",
//                    requireContext().packageName
//                )
//                audioQueue.add(counterResourceId)
//            }
//        }
//
//        // Start playing the first audio if queue is not empty
//        if (audioQueue.isNotEmpty()) {
//            playNextAudio()
//        }
//    }
//
//    private fun playNextAudioEnglish(onComplete: (() -> Unit)? = null) {
//        if (englishAudioQueue.isNotEmpty()) {
//            val resourceId = englishAudioQueue.removeAt(0)
//
//            if (resourceId != 0) {
//                mediaPlayer = MediaPlayer.create(requireContext(), resourceId)
//                mediaPlayer?.setOnCompletionListener {
//                    playNextAudioEnglish(onComplete) // Recursively play the next audio
//                }
//                mediaPlayer?.start()
//            } else {
//                Log.e("AudioError", "Invalid resource ID: $resourceId")
//                playNextAudioEnglish(onComplete) // Skip this invalid resource and continue to the next one
//            }
//        } else {
//            // All English audio has been played, now call the onComplete callback
//            onComplete?.invoke()
//        }
//
//    }
//
//
//    private fun playNextAudio(onComplete: (() -> Unit)? = null) {
//        if (audioQueue.isNotEmpty()) {
//            val resourceId = audioQueue.removeAt(0)
//
//            if (resourceId != 0) {
//                mediaPlayer = MediaPlayer.create(requireContext(), resourceId)
//                mediaPlayer?.setOnCompletionListener {
//                    playNextAudio(onComplete) // Recursively play the next audio
//                }
//                mediaPlayer?.start()
//            } else {
//                Log.e("AudioError", "Invalid resource ID: $resourceId")
//                playNextAudio(onComplete) // Skip this invalid resource and continue to the next one
//            }
//        } else {
//            onComplete?.invoke() // When all audio is finished, trigger onComplete
//        }
//    }


    private fun observerCurrentTimeViewModel() {

        getCurrentTimeViewModel.timeResponse.observe(viewLifecycleOwner) { timeResponse ->
            val (date, time) = timeResponse.msgEn!!.split(" ")

            binding.tvTime.text = time
            binding.tvDate.text = date

        }


        getCurrentTimeViewModel.errorResponse.observe(viewLifecycleOwner) {
            Log.v("error", "error")
        }

    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        audioQueue.clear() // Clear the audio queue
//
//    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(runnable)
        timeRefreshHandler.removeCallbacks(timeRefreshRunnable) // Stop time refresh
        // Stop the auto-refresh when fragment is not visible
        //    screenHandler.removeCallbacks(runnable) // Stop the auto-refresh when fragment is not visible

        scrollMsgsHandler.removeCallbacks(scrollMsgsRunnable) // Stop the scroll messages refresh
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove any pending callbacks to avoid memory leaks
        //timehandler.removeCallbacksAndMessages(null)
        scrollMsgsHandler.removeCallbacksAndMessages(null)

    }
}