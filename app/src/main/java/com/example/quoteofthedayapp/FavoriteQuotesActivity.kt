package com.example.quoteofthedayapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class FavoriteQuotesActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var favorites: MutableList<String>
    private lateinit var backIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_quotes)

        listView = findViewById(R.id.listView)
        backIcon = findViewById(R.id.backIcon)
        favorites = getFavorites()

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, favorites)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val quote = favorites[position]
            showDeleteDialog(quote, position)
        }

        backIcon.setOnClickListener {
            // Navigate back to the MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun getFavorites(): MutableList<String> {
        val sharedPref = getSharedPreferences("favorite_quotes", Context.MODE_PRIVATE)
        val quotes = sharedPref.getStringSet("quotes", setOf())?.toMutableList() ?: mutableListOf()
        return quotes
    }

    private fun showDeleteDialog(quote: String, position: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Do you want to delete this quote?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                deleteQuote(quote, position)
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun deleteQuote(quote: String, position: Int) {
        val sharedPref = getSharedPreferences("favorite_quotes", Context.MODE_PRIVATE)
        val quotes = sharedPref.getStringSet("quotes", setOf())?.toMutableSet() ?: mutableSetOf()
        quotes.remove(quote)
        with(sharedPref.edit()) {
            putStringSet("quotes", quotes)
            apply()
        }
        favorites.removeAt(position)
        adapter.notifyDataSetChanged()
    }
}
