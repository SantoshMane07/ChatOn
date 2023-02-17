package com.example.chaton.models

class User {
    var id:String=""
    var name:String=""
    var phone:String=""
    var email:String=""
    var imgUri:String=""
    var status:String=""

    constructor(){
    }
    constructor(id:String,name:String,phone:String,email:String,imgUri:String,status:String){
        this.name =name
        this.email=email
        this.phone=phone
        this.imgUri=imgUri
        this.id=id
        this.status=status
    }
}