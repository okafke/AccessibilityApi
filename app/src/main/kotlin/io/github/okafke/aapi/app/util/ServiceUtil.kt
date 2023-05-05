package io.github.okafke.aapi.app.util

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context.ACCESSIBILITY_SERVICE
import android.view.accessibility.AccessibilityManager

fun AccessibilityService.isEnabled(): Boolean {
    val am = this.getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
    val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
    return enabledServices
        .map { service -> service.resolveInfo.serviceInfo }
        .filter { info -> info.packageName.equals(this.applicationContext.packageName) }
        .any { info -> info.name.equals(this::class.java.name) }
}
