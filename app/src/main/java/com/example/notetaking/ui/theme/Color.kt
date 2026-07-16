package com.example.notetaking.ui.theme

import androidx.compose.ui.graphics.Color

// Modern light theme: clean off-white background, bold royal blue accent.
val DarkBackground = Color(0xFFF7F8FC)       // main background (name kept for compat, now light)
val SurfaceLeft = Color(0xFFFFFFFF)          // card / panel background
val SurfaceField = Color(0xFFEFF1F8)         // input field background
val BorderColor = Color(0xFFDCE1F0)          // input border / divider

val Gold = Color(0xFF2541E8)                 // primary accent (buttons, links) — royal blue
val GoldHover = Color(0xFF4259F0)            // hover/pressed state — slightly lighter

val TextPrimary = Color(0xFF14172B)          // headings / primary text
val TextTagline = Color(0xFF5C6280)          // field labels
val TextMuted = Color(0xFF7B81A0)            // secondary text, "forgot password" link
val TextFaint = Color(0xFFA0A5C0)            // low-emphasis prompt text
val TextPlaceholder = Color(0xFFB4B9D4)      // input placeholder text

val ErrorText = Color(0xFFD33F49)            // error message text
val ErrorBg = Color(0x14D33F49)              // error banner background (8% alpha)
val ErrorBorder = Color(0x40D33F49)          // error banner border (25% alpha)