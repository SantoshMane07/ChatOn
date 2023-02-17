package com.example.chaton.models

class Messeage {
    var messeage:String=""
    var timestamp:Long=0
    var senderID:String=""

    constructor(){

    }

    constructor(messeage: String, timestamp: Long, senderID: String) {
        this.messeage = messeage
        this.timestamp = timestamp
        this.senderID = senderID
    }
}