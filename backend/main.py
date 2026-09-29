from fastapi import FastAPI, WebSocket, WebSocketDisconnect
from pydantic import BaseModel


app = FastAPI(
    title="VoiceShield AI",
    description="Real-time AI voice protection backend",
    version="1.0.0"
)


# --------------------------------------------------
# Request Models
# --------------------------------------------------

class RiskRequest(BaseModel):
    call_id: str
    caller_number: str
    risk: int


class ConversationRequest(BaseModel):
    caller_known: bool
    asks_otp: bool
    asks_money: bool
    urgent_threat: bool
    suspicious_keywords: int


class VoiceAnalysisRequest(BaseModel):
    voice_authenticity: int


# --------------------------------------------------
# Basic Routes
# --------------------------------------------------

@app.get("/")
def home():
    return {
        "status": "online",
        "service": "VoiceShield AI Backend"
    }


@app.get("/health")
def health():
    return {
        "status": "healthy"
    }


# --------------------------------------------------
# Risk Level
# --------------------------------------------------

def get_risk_level(risk: int) -> str:
    if risk <= 30:
        return "LOW"
    elif risk <= 60:
        return "MEDIUM"
    elif risk <= 80:
        return "HIGH"
    else:
        return "CRITICAL"


# --------------------------------------------------
# Basic Risk API
# --------------------------------------------------

@app.post("/risk")
def calculate_risk(request: RiskRequest):

    risk = max(0, min(request.risk, 100))

    return {
        "call_id": request.call_id,
        "caller_number": request.caller_number,
        "risk": risk,
        "risk_level": get_risk_level(risk),
        "action": (
            "CONTINUE" if risk <= 30
            else "WARNING" if risk <= 60
            else "MFA / CALLBACK" if risk <= 80
            else "BLOCK / ESCALATE"
        )
    }


# --------------------------------------------------
# Conversation Risk Engine
# --------------------------------------------------

def calculate_conversation_risk(
    data: ConversationRequest
) -> int:

    risk = 0

    # Unknown caller
    if not data.caller_known:
        risk += 15

    # OTP request
    if data.asks_otp:
        risk += 35

    # Money / financial request
    if data.asks_money:
        risk += 35

    # Urgency / threat
    if data.urgent_threat:
        risk += 10

    # Suspicious keywords
    risk += min(data.suspicious_keywords * 5, 20)

    return min(risk, 100)


@app.post("/conversation-risk")
def conversation_risk(request: ConversationRequest):

    risk = calculate_conversation_risk(request)

    return {
        "risk": risk,
        "risk_level": get_risk_level(risk),
        "action": (
            "CONTINUE" if risk <= 30
            else "WARNING" if risk <= 60
            else "MFA / CALLBACK" if risk <= 80
            else "BLOCK / ESCALATE"
        )
    }


# --------------------------------------------------
# Voice Authenticity Risk
# --------------------------------------------------

def calculate_voice_risk(
    data: VoiceAnalysisRequest
) -> int:

    authenticity = max(
        0,
        min(data.voice_authenticity, 100)
    )

    # High authenticity = low risk
    risk = 100 - authenticity

    return risk


@app.post("/voice-risk")
def voice_risk(request: VoiceAnalysisRequest):

    risk = calculate_voice_risk(request)

    return {
        "voice_authenticity": request.voice_authenticity,
        "voice_risk": risk,
        "risk_level": get_risk_level(risk)
    }


# --------------------------------------------------
# Real-Time WebSocket Risk
# --------------------------------------------------

@app.websocket("/ws/risk")
async def risk_websocket(websocket: WebSocket):

    await websocket.accept()

    try:

        while True:

            data = await websocket.receive_json()

            risk = int(data.get("risk", 0))

            risk = max(0, min(risk, 100))

            await websocket.send_json({
                "risk": risk,
                "risk_level": get_risk_level(risk)
            })

    except WebSocketDisconnect:

        print("Android client disconnected")