package com.mavalore.tricenari.domain.models.dynamicValues

data class DynamicValues(
    val all_categories: List<AllCategory>,
    val all_interests: List<AllInterest>,
    val popularArticles: List<Int>,
    val popularTags: List<String>,
    val popular_superwomen: List<PopularSuperwomen>
)