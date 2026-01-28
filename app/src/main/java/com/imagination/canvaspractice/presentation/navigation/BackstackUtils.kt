package com.imagination.canvaspractice.presentation.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlin.collections.removeLastOrNull

typealias BackStack = SnapshotStateList<Screen>

fun BackStack.pop(): Screen? = this.removeLastOrNull()

fun BackStack.push(screen: Screen) = this.add(screen)

fun BackStack.replace(screen: Screen) {
    this.pop()
    this.push(screen)
}