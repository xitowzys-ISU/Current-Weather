package com.example.currentweatherdatabinding

data class City(
    val coords: Coord,
    val district: String,
    val name: String,
    val population: String,
    val subject: String
) {
    data class Coord(val lat: Float, val lon: Float)
}
