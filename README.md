
# VoiceShield AI 🛡️

AI-assisted protection against voice cloning,
voice impersonation and suspicious calls.

## Overview
VoiceShield AI is a prototype that aims to help
users identify potentially fraudulent calls through
voice analysis and contextual risk assessment.

## Features
- Android application using Kotlin and Jetpack Compose
- FastAPI backend
- Risk assessment API
- WebSocket support for live risk updates
- Risk levels: LOW, MEDIUM, HIGH and CRITICAL

## Technology Stack
- Kotlin
- Jetpack Compose
- Python
- FastAPI
- WebSockets
- Firebase (when configured)

## Project Structure
- `app/` — Android application
- `backend/` — Python backend
- `gradle/` — Gradle wrapper configuration

## Setup

### Android
1. Clone this repository.
2. Open the project in Android Studio.
3. Configure any required Firebase settings.
4. Sync Gradle and run the app.

### Backend
1. Install Python 3.11 or another compatible version.
2. Install dependencies from `backend/requirements.txt`.
3. Start the FastAPI server using the documented
   command for this project.

## Project Status
Prototype under development.

Live risk updates do not by themselves prove
accurate voice-cloning detection. Detection
performance requires separate testing and validation.

## Team
Abhishek Dubey and team