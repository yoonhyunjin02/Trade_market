// 챗봇용 WebSocket 연결 변수
let botSocket = null;

window.openBotChat = function() {
    console.log("openBotChat() 실행됨!");
    window.isBotChat = true; // 챗봇 모드 플래그
    toggleChatRoomButtons(false); // 오른쪽 버튼 숨기기
    showFaqButtons(true);

    const placeholder = document.getElementById("conversation-placeholder");
    const conversation = document.getElementById("chat-conversation");
    const messagesContainer = document.getElementById("messages-container");

    // 기존 일반 채팅방 메시지 초기화
    messagesContainer.innerHTML = "";

    // UI 전환
    if (placeholder) placeholder.style.display = "none";
    if (conversation) conversation.style.display = "flex";

    // 챗봇 전용 헤더 UI 변경
    document.getElementById("partner-info").innerHTML = `
        <div class="bot-header-icon">
            <img src="/images/icons/chat-bot.svg" alt="AI 챗봇">
        </div>
        <span class="partner-name">AI 챗봇</span>
    `;

    // 상품 정보 카드 숨기기
    const productInfoCard = document.querySelector(".product-info-card");
    if (productInfoCard) productInfoCard.style.display = "none";

    // 챗봇 전용 FAQ 버튼 영역 표시
    const faqButtonsContainer = document.getElementById("bot-faq-buttons");
    if (faqButtonsContainer) faqButtonsContainer.style.display = "flex";

    // 현재 시간
    const now = new Date();
    const timeString = now.toLocaleTimeString("ko-KR", {
        hour: "numeric",
        minute: "2-digit",
        hour12: true
    });

    // 사용자 이름
    const userName = currentUserName || "사용자";

    // 챗봇 첫 인사 메시지 출력
    messagesContainer.innerHTML = `
        <div class="message-wrapper">
            <div class="message received">
                <div class="message-content">
                    안녕하세요! <strong>${userName}</strong> 님! 궁금한 점을 빠르게 도와드릴게요.<br>
                    어떤 점이 궁금하신가요?
                </div>
                <div class="message-time">${timeString}</div>
            </div>
        </div>
    `;

    // 챗봇 WebSocket 연결
    connectBotWebSocket();
}

function connectBotWebSocket() {
    if (botSocket && botSocket.readyState === WebSocket.OPEN) {
        console.log("챗봇 소켓 이미 연결됨");
        return;
    }

    const protocol = window.location.protocol === "https:" ? "wss:" : "ws:";
    const botUrl = `${protocol}//${window.location.host}/ws/chatbot`;
    botSocket = new WebSocket(botUrl);

    botSocket.onopen = () => {
        console.log("챗봇 WebSocket 연결 완료!");
    };

    botSocket.onmessage = (event) => {
        console.log("챗봇 응답 수신:", event.data);
        const msg = event.data;

        // 서버에서 초기화 응답이 왔으면 그냥 처음 챗봇 화면으로 복귀
        if (msg.includes("대화가 초기화되었습니다")) {
            openBotChat();  // 기존 메시지 삭제 + 인사 + FAQ 버튼까지 초기 화면 복귀
            return;
        }

        // 일반 메시지라면 그대로 추가
        addBotAnswer(msg);
    };

    // 메시지 전체 삭제 함수
    function clearChatUI() {
        const container = document.getElementById("messages-container");
        if (container) {
            container.innerHTML = "";
        }
    }

    botSocket.onclose = () => {
        console.warn("챗봇 WebSocket 연결 종료됨 (5초 후 재연결 시도)");
        setTimeout(connectBotWebSocket, 5000);
    };

    botSocket.onerror = (err) => {
        console.error("챗봇 WebSocket 에러:", err);
    };
}

// === 챗봇 응답을 UI에 추가 ===
function addBotAnswer(text) {
    const now = new Date();
    const timeString = now.toLocaleTimeString("ko-KR", {
        hour: "numeric",
        minute: "2-digit",
        hour12: true
    });

    const msgDiv = document.createElement("div");
    msgDiv.className = "message received";
    msgDiv.innerHTML = `
        <div class="message-content">${text}</div>
        <div class="message-time">${timeString}</div>
    `;

    const container = document.getElementById("messages-container");
    container.appendChild(msgDiv);

    msgDiv.scrollIntoView({ behavior: "smooth", block: "center" });
}

function sendFaq(question) {
    // 버튼 클릭 시 질문 UI 한 번만 추가
    addBotMessage(question);

    // 서버에만 전송 (UI 중복 추가 X)
    if (!botSocket || botSocket.readyState !== WebSocket.OPEN) {
        alert("챗봇 연결이 끊겼습니다. 새로고침 후 다시 시도해주세요.");
        return;
    }
    botSocket.send(question);
}

function addBotMessage(text) {
    const now = new Date();
    const timeString = now.toLocaleTimeString("ko-KR", {
        hour: "numeric",
        minute: "2-digit",
        hour12: true
    });

    const msgDiv = document.createElement("div");
    msgDiv.className = "message sent";
    msgDiv.innerHTML = `<div class="message-content">${text}</div><div class="message-time">${timeString}</div>`;
    document.getElementById("messages-container").appendChild(msgDiv);

    // 질문 말풍선 위치로 스크롤 이동
    msgDiv.scrollIntoView({ behavior: "smooth", block: "center" });
}

function fetchBotAnswer(question) {
    // 이제 로컬 FAQ 로직은 서버가 처리
    if (!botSocket || botSocket.readyState !== WebSocket.OPEN) {
        alert("챗봇 연결이 끊겼습니다. 새로고침 후 다시 시도해주세요.");
        return;
    }

    // 내 질문 UI에 먼저 출력
    addBotMessage(question);

    // 서버(ChatBotWebSocketHandler)로 질문 그대로 전송
    botSocket.send(question);
}

function resetBotChat() {
    if (!botSocket || botSocket.readyState !== WebSocket.OPEN) {
        alert("챗봇 연결이 끊겼습니다.");
        return;
    }
    botSocket.send("__RESET_CHAT__");  // 서버에 대화 초기화 요청
}

document.addEventListener("DOMContentLoaded", () => {
  const resetBtn = document.getElementById("resetChatBtn");
  if (resetBtn) {
    resetBtn.addEventListener("click", () => {
      if (!botSocket || botSocket.readyState !== WebSocket.OPEN) {
        alert("챗봇 연결이 끊겼습니다. 새로고침 후 다시 시도해주세요.");
        return;
      }

      if (confirm("정말 대화를 초기화할까요?")) {
        botSocket.send("__RESET_CHAT__");
      }
    });
  }
});