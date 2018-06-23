package com.watchme.model

data class Manager(var loginType: String,
                   var name: String,
                   var phone: String,
                   var token: String,
                   var id: String,
                   var loginTypeAndId: String = String.format("%s_%s", loginType, id))
