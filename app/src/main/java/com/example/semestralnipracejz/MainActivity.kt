package com.example.semestralnipracejz

import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {

    private lateinit var cardImageView: ImageView
    private lateinit var cardCounterTextView: TextView
    private lateinit var cardCounterTextViewUpsideDown: TextView
    private lateinit var cardRuleTextView: TextView
    private lateinit var cardRuleTextViewRight: TextView
    private val deck = mutableListOf<Pair<Int, String>>()
    private var currentIndex = 0
    private var isHolding = false
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cardImageView = findViewById(R.id.cardImageView)
        cardCounterTextView = findViewById(R.id.cardCounterTextView)
        cardCounterTextViewUpsideDown = findViewById(R.id.cardCounterTextViewUpsideDown)
        cardRuleTextView = findViewById(R.id.cardRuleTextView)
        cardRuleTextViewRight = findViewById(R.id.cardRuleTextViewRight)

        // Naplnění balíčku karet (každý typ 4x s pravidly)
        val cardImages = listOf(
            R.drawable.card_2,
            R.drawable.card_3,
            R.drawable.card_4,
            R.drawable.card_5,
            R.drawable.card_6,
            R.drawable.card_7,
            R.drawable.card_8,
            R.drawable.card_9,
            R.drawable.card_10,
            R.drawable.card_j,
            R.drawable.card_q,
            R.drawable.card_k,
            R.drawable.card_a
        )
//        val cardRules = resources.getStringArray(R.array.card_rules).toList()

//        cardImages.forEachIndexed { index, imageResId ->
//            repeat(4) { // Každý typ karty se opakuje čtyřikrát
//                deck.add(Pair(imageResId, cardRules[index]))
//            }
//        }
//
//        //cardRules[index] zmenit na db
//
//        deck.shuffle()
//// ...
        database = Firebase.database.reference

        val db = Firebase.firestore
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        Firebase.firestore.firestoreSettings = settings

        //

        cardImages.forEachIndexed { index, imageResId ->
            repeat(4) { // Každý typ karty se opakuje čtyřikrát
                val docRef = db.collection("cards").document((index+1).toString()) // Load cards from Firestore
                docRef.get()
                    .addOnSuccessListener { document ->
                        deck.add(Pair(imageResId, document.data?.get("rule").toString()))
                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "get failed with ", exception)
                    }
            }
        }
        deck.shuffle()

            ///
        //database ########

        // Zobrazit rub karty na začátku
        cardImageView.setImageResource(R.drawable.card_back)

        // Nastavit počáteční odpočet karet
        updateCardCounter()
        // Nastavení podržení pro zobrazení karty
        cardImageView.setOnTouchListener { _, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    isHolding = true
                    deck.shuffle()
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (isHolding) showNextCard()
                    }, 1000) // Podržení alespoň 1 sekundu
                }

                android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                    isHolding = false
                }
            }
            true
        }
    }

    private fun showNextCard() {
        if (currentIndex < deck.size) {
            val (cardImageResId, rule) = deck[currentIndex]

            cardImageView.setImageResource(cardImageResId)
            cardRuleTextView.text = rule
            cardRuleTextViewRight.text = rule

            currentIndex++
            updateCardCounter()
        } else {
            cardImageView.setImageResource(R.drawable.card_empty) // Pokud jsou všechny karty vyčerpány
            cardRuleTextView.text = "No cards left."
            cardRuleTextViewRight.text = "No cards left."
        }
    }




    private fun updateCardCounter() {
        val cardsLeft = deck.size - currentIndex
        cardCounterTextView.text = "Cards left: $cardsLeft"
        cardCounterTextViewUpsideDown.text = "Cards left: $cardsLeft"
    }






}





