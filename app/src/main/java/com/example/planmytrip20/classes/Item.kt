package com.example.planmytrip20.classes

data class Item(
    val url: String, // Resource ID of the item's image
    val Name: String,       // Name of the item
    val description: String // Description of the item
){
    constructor(map: Map<String, Any>) : this(
        url = map["url"] as String, // We'll load the image using the URL later
        Name = map["name"] as String,
        description = map["description"] as String
    )
}
