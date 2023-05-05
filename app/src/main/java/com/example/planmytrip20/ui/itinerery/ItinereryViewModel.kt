package com.example.planmytrip20.ui.itinerery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ItinereryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is itinerery Fragment"
    }
    val text: LiveData<String> = _text

    fun setText(str:String)
    {
        _text.value = str
    }
}