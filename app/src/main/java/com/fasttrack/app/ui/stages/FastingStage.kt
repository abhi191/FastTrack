package com.fasttrack.app.ui.stages

data class FastingStage(
    val id: Int,
    val emoji: String,
    val name: String,
    val timeRange: String,
    val thresholdHours: Float,
    val description: String,
)

val fastingStages = listOf(
    FastingStage(0, "🍽️", "Fed State", "0 – 4 hours", 0f,
        "Blood sugar and insulin levels are elevated as your body digests and absorbs nutrients from your last meal."),
    FastingStage(1, "⏳", "Early Fasting", "4 – 8 hours", 4f,
        "Blood sugar returns to baseline. Your body begins tapping into stored glycogen in the liver and muscles for energy."),
    FastingStage(2, "🔋", "Glycogen Depletion", "8 – 12 hours", 8f,
        "Liver glycogen reserves are being used up. The metabolic switch from glucose to fat begins."),
    FastingStage(3, "🔥", "Fat Burning Begins", "12 – 16 hours", 12f,
        "Your body switches to fat as its primary fuel source. Ketone levels start rising, providing alternative brain fuel."),
    FastingStage(4, "⚡", "Ketosis", "16 – 24 hours", 16f,
        "Significant fat burning underway. Ketone bodies fuel the brain and body. Growth hormone levels start increasing."),
    FastingStage(5, "♻️", "Autophagy", "24 – 48 hours", 24f,
        "Cellular self-cleaning activates. Cells break down and recycle damaged proteins and organelles — a key anti-aging mechanism."),
    FastingStage(6, "🧬", "Deep Autophagy", "48 – 72 hours", 48f,
        "Autophagy reaches peak levels. Immune system regeneration begins. Growth hormone increases significantly."),
    FastingStage(7, "🌟", "Stem Cell Renewal", "72+ hours", 72f,
        "The immune system regenerates from stem cells. Profound cellular renewal occurs throughout the body."),
)
