package com.example.currentweatherdatabinding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import com.example.currentweatherdatabinding.databinding.ActivityMainConstrainBinding
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import java.util.*

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var gson: Gson
    private lateinit var citiesRaw: Array<City>
    private lateinit var city: City

    lateinit var binding: ActivityMainConstrainBinding
    lateinit var temp: CityTemperature

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_constrain)

        this.temp = CityTemperature(name = getString(R.string.select_city))
        binding.city = this.temp

        val spinner = findViewById<Spinner>(R.id.spinner)

        gson = Gson()
        val citiesStream = resources.openRawResource(R.raw.russian_cities)
        citiesRaw = gson.fromJson(InputStreamReader(citiesStream), Cities::class.java).cities

        val cityNames: ArrayList<String> = ArrayList()
        for (city in citiesRaw) {
            cityNames.add(city.name)
        }

        val adapter = ArrayAdapter(
            this,
            R.layout.color_spinner,
            cityNames
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = this
    }

    suspend fun loadWeather() {
        try {
            val weatherURL =
                "https://api.openweathermap.org/data/2.5/weather?lat=${city.coords.lat}&lon=${city.coords.lon}&appid=${BuildConfig.API_KEY_OPEN_WEATHER_MAP}&units=metric"
            val stream = URL(weatherURL).content as InputStream

            val data = Scanner(stream).nextLine()

            val parser = JsonParser.parseString(data).asJsonObject

            this.temp.temperature = parser.get("main").asJsonObject.get("temp").toString().toFloat()
            this.temp.windSpeed = parser.get("wind").asJsonObject.get("speed").toString().toFloat()
            this.binding.city = this.temp


        } catch (e: Exception) {
            this.temp.name = getString(R.string.error_service)
            this.binding.city = this.temp
        }
    }

    @DelicateCoroutinesApi
    fun onClick(v: View) {
        GlobalScope.launch(Dispatchers.IO) {
            loadWeather()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val cityIndex = position
        city = citiesRaw[cityIndex]
        this.temp.name = city.name
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

}