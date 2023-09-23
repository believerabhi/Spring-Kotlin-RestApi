package com.abhi.example.springbootkotlin

import java.time.LocalDateTime

data class Article(
        var title: String,
        var content:String,
        val createdAt:LocalDateTime = LocalDateTime.now(),
        var slug: String = title.toSlug()
)
