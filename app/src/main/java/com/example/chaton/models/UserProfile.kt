package com.example.chaton.models

class UserProfile{
    var name:String=""
    var imgUri:String=""
    var status:String=""

    constructor(){
    }
    constructor(id:String,name:String,phone:String,email:String,imgUri:String,status:String){
        this.name =name
        this.imgUri=imgUri
        this.status=status
    }
}