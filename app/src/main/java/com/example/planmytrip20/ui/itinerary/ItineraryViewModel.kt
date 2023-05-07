package com.example.planmytrip20.ui.itinerary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ItineraryViewModel : ViewModel() {

    private val _nextLocationIndex = MutableLiveData<Int>().apply {
        value = 2
    }

    private val _prevLocationIndex = MutableLiveData<Int>().apply {
        value = 1
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is itinerary Fragment"
    }
    val text: LiveData<String> = _text
    val nextLocationIndex: LiveData<Int> = _nextLocationIndex
    val prevLocationIndex: LiveData<Int> = _prevLocationIndex

    fun setText(str:String)
    {
        _text.value = str
    }

    fun setNextLocationIndex(value:Int)
    {
        _nextLocationIndex.value = value
    }

    fun setPrevLocationIndex(value:Int)
    {
        _prevLocationIndex.value = value
    }

}