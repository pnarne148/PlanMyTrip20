package com.example.planmytrip20.api

import android.util.Log
import com.example.planmytrip20.classes.database
import com.google.firebase.firestore.ktx.toObject

class DatabaseRequest {

    companion object {
        const val TAG = "DatabaseRequest"

        // Get data
        fun getQuery(collection: String, user_id: String, where: String) : ArrayList<Any> {
            var resultList : ArrayList<Any> = ArrayList()
            database.db.collection(collection)
                .get()
                .addOnCompleteListener{ task ->
                    if(task.isSuccessful){
                        Log.d(TAG, "Testing query response => ${task.result}")
                        for(doc in task.result){
                            resultList.add(doc)
                        }
                        Log.d(TAG, "Result List => $resultList")
                        return@addOnCompleteListener
                    }
                }
            return resultList
        }
    }

}