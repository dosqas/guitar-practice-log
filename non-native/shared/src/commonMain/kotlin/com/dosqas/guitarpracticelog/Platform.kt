package com.dosqas.guitarpracticelog

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform