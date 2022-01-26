package com.example.pokeman.models

import android.location.Location

class pokemon {

    var name:String?=null
    var description:String?=null
    var Image:Int?=null
    var Power:Double?=null
    var location:Location?=null
    var isCatch:Boolean=false


    constructor(
        name: String?,
        description: String?,
        Image: Int?,
        Power: Double?,
        lat: Double,
        long: Double,
    ) {
        this.name = name
        this.description = description
        this.Image = Image
        this.Power = Power
        this.location=Location(name)
        this.location!!.latitude=lat
        this.location!!.longitude=long
        this.isCatch = isCatch
    }



}