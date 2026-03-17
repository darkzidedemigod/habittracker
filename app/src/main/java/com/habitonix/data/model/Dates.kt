package com.habitonix.data.model

import java.time.LocalDate

fun LocalDate.toEpochDayLong(): Long = this.toEpochDay()
fun Long.toLocalDate(): LocalDate = LocalDate.ofEpochDay(this)

