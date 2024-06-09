package com.example.quoteofthedayapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var tvQuote: TextView
    private lateinit var tvPoet: TextView
    private lateinit var ivShare: ImageView
    private lateinit var ivFavorite: ImageView
    private lateinit var ivViewFavorites: ImageView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var searchView: SearchView

    private val quotes = listOf(
        Pair("The only way to do great work is to love what you do.", "Steve Jobs"),
        Pair("Life is what happens when you’re busy making other plans.", "John Lennon"),
        Pair("Get busy living or get busy dying.", "Stephen King"),
        Pair("You only live once, but if you do it right, once is enough.", "Mae West"),
        Pair("The purpose of our lives is to be happy.", "Dalai Lama")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvQuote = findViewById(R.id.tvQuote)
        tvPoet = findViewById(R.id.tvPoet)
        ivShare = findViewById(R.id.ivShare)
        ivFavorite = findViewById(R.id.ivFavorite)
        ivViewFavorites = findViewById(R.id.ivViewFavorites)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        searchView = findViewById(R.id.searchView)

        checkAndUpdateQuote()

        ivShare.setOnClickListener {
            shareQuote()
        }

        ivFavorite.setOnClickListener {
            addToFavorites()
        }

        ivViewFavorites.setOnClickListener {
            val intent = Intent(this, FavoriteQuotesActivity::class.java)
            startActivity(intent)
        }

        swipeRefreshLayout.setOnRefreshListener {
            refreshQuote()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchQuote(it) }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchQuote(it) }
                return false
            }
        })
    }

    private fun checkAndUpdateQuote() {
        val sharedPref = getSharedPreferences("quote_preferences", Context.MODE_PRIVATE)
        val lastUpdatedDate = sharedPref.getString("last_updated_date", null)
        val currentDateString = getCurrentDateString()

        if (lastUpdatedDate == null || lastUpdatedDate != currentDateString) {
            showRandomQuote()
            val editor = sharedPref.edit()
            editor.putString("last_updated_date", currentDateString)
            editor.apply()
        } else {
            val lastQuote = sharedPref.getString("last_quote", null)
            val lastPoet = sharedPref.getString("last_poet", null)
            if (lastQuote != null && lastPoet != null) {
                tvQuote.text = lastQuote
                tvPoet.text = lastPoet
            } else {
                showRandomQuote()
            }
        }
    }

    private fun getCurrentDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun showRandomQuote() {
        val randomQuote = quotes[Random().nextInt(quotes.size)]
        tvQuote.text = randomQuote.first
        tvPoet.text = randomQuote.second
        saveLastQuote(randomQuote)
    }

    private fun saveLastQuote(quote: Pair<String, String>) {
        val sharedPref = getSharedPreferences("quote_preferences", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("last_quote", quote.first)
        editor.putString("last_poet", quote.second)
        editor.apply()
    }

    private fun refreshQuote() {
        showRandomQuote()
        swipeRefreshLayout.isRefreshing = false
    }

    private fun shareQuote() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "“${tvQuote.text}” - ${tvPoet.text}")
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share quote via"))
    }

    private fun addToFavorites() {
        val sharedPref = getSharedPreferences("favorite_quotes", Context.MODE_PRIVATE)
        val quotes = sharedPref.getStringSet("quotes", setOf())?.toMutableSet() ?: mutableSetOf()
        val currentQuote = "“${tvQuote.text}” - ${tvPoet.text}"

        if (quotes.contains(currentQuote)) {
            showSnackbar("Quote is already saved")
        } else {
            quotes.add(currentQuote)
            val editor = sharedPref.edit()
            editor.putStringSet("quotes", quotes)
            editor.apply()
            showSnackbar("Quote saved successfully")
        }
    }

    private fun searchQuote(query: String) {
        val foundQuote = quotes.find { it.first.contains(query, true) }
        if (foundQuote != null) {
            tvQuote.text = foundQuote.first
            tvPoet.text = foundQuote.second
        } else {
            tvQuote.text = "No quote found"
            tvPoet.text = ""
        }
    }

    private fun showSnackbar(message: String) {
        val snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)
        snackbar.view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
        snackbar.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        snackbar.show()
    }
}
