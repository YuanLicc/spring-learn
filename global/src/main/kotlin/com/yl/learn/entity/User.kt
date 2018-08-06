package com.yl.learn.entity

import com.yl.learn.exception.IllegalParameterException
import java.io.Serializable

public class User : Serializable {

    private final val defaultName : String = "宝宝"

    var id : Int = -1;

    var name : String = defaultName
        /*set(value) {
            name = value.trim()
        }*/

    var age : Int = 0
        /*set(value) {
            if (value <= 0) {
                throw IllegalParameterException("User.age greater than zero, the value: ${value} is wrong")
            }
            else {
                age = value
            }
        }*/

    var sex : String = "未知"
        /*set(value) {
            when {
                value.isNullOrEmpty() -> sex = "未知"
                value.trim().isEmpty() -> sex = "未知"
                else -> sex = value.trim()
            }
        }*/

    var password : String = "123456"
        /*set(value) {
            if (value.isNullOrEmpty())
                throw NullPointerException("")
            else
                password = value.trim()
        }*/

    constructor(){}

}