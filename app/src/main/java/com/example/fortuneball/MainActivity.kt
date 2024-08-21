package com.example.fortuneball

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    private lateinit var imageViewBall: ImageView
    private lateinit var viewModel: ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        imageViewBall = findViewById(R.id.imageViewBall)
        viewModel = (ViewModelProvider(this).get(ShakeDetectorViewModel::class.java))
        (viewModel as ShakeDetectorViewModel).application = application
        imageViewBall.setOnClickListener{
            setRandomImage()
        }

        (viewModel as ShakeDetectorViewModel).needChangeImage.observe(this) { newValue ->
            if (newValue) {
                setRandomImage()
                (viewModel as ShakeDetectorViewModel).resetNeedChangeImage()
            }
        }

        (viewModel as ShakeDetectorViewModel).registerSensorListener()
    }

    private fun setRandomImage() {
        val randomChoice = Random.nextInt(20)+1
        val nameOfImg = "magic_8_ball_$randomChoice"
        val imageResource = resources.getIdentifier(nameOfImg, "drawable", packageName)
        imageViewBall.setImageResource(imageResource)
    }

    override fun onPause() {
        super.onPause()
        (viewModel as ShakeDetectorViewModel).unregisterSensorListener()
    }

    override fun onResume() {
        super.onResume()
        (viewModel as ShakeDetectorViewModel).registerSensorListener()
    }
}
