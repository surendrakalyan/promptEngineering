package com.example.promptengineeringlab

data class CompareResponse(
    val zero_shot: String? = null,
    val few_shot: String? = null,
    val explanation_based: String? = null,
    val error: String? = null
)