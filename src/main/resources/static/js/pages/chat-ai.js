// 챗봇용 WebSocket 연결 변수
let botSocket = null;

window.openBotChat = function() {
    console.log("✅ openBotChat() 실행됨!");
    window.isBotChat = true;                 // 챗봇 모드
    toggleChatRoomButtons(false);     // 오른쪽 버튼 숨기기

    const placeholder = document.getElementById("conversation-placeholder");
    const conversation = document.getElementById("chat-conversation");
    const messagesContainer = document.getElementById("messages-container");

    // 기존 일반 채팅방 메시지 초기화 (완전 리셋)
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

    // FAQ 버튼 추가
    addFaqButtons(messagesContainer);

    // ✅ 챗봇용 WebSocket 연결 추가
    connectBotWebSocket();

    // "대화 종료" 버튼 추가
    addResetChatButton(messagesContainer);
}

function connectBotWebSocket() {
    if (botSocket && botSocket.readyState === WebSocket.OPEN) {
        console.log("🤖 챗봇 소켓 이미 연결됨");
        return;
    }

    const protocol = window.location.protocol === "https:" ? "wss:" : "ws:";
    const botUrl = `${protocol}//${window.location.host}/ws/chatbot`;
    botSocket = new WebSocket(botUrl);

    botSocket.onopen = () => {
        console.log("🤖 챗봇 WebSocket 연결 완료!");
    };

    botSocket.onmessage = (event) => {
        console.log("🤖 챗봇 응답 수신:", event.data);
        addBotAnswer(event.data);
    };

    botSocket.onclose = () => {
        console.warn("🤖 챗봇 WebSocket 연결 종료됨 (5초 후 재연결 시도)");
        setTimeout(connectBotWebSocket, 5000);
    };

    botSocket.onerror = (err) => {
        console.error("🤖 챗봇 WebSocket 에러:", err);
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
    addBotMessage(question);
    fetchBotAnswer(question);
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
    // **이제 로컬 FAQ 로직은 서버가 처리**
    if (!botSocket || botSocket.readyState !== WebSocket.OPEN) {
        alert("⚠️ 챗봇 연결이 끊겼습니다. 새로고침 후 다시 시도해주세요.");
        return;
    }

    // 내 질문 UI에 먼저 출력
    addBotMessage(question);

    // 서버(ChatBotWebSocketHandler)로 질문 그대로 전송
    botSocket.send(question);
}

function addFaqButtons(container) {
    // 기존 FAQ 버튼이 있으면 먼저 삭제
    const oldFaq = container.querySelector(".faq-buttons");
    if (oldFaq) oldFaq.remove();

    // 새 FAQ 버튼 생성
    const faqDiv = document.createElement("div");
    faqDiv.className = "faq-buttons";
    faqDiv.innerHTML = `
        <button onclick="sendFaq('법적으로 중고 거래를 금지하는 품목이 있나요?')">
            법적으로 중고 거래를 금지하는 품목이 있나요?
        </button>
        <button onclick="sendFaq('당근 마켓에서의 거래 매너를 알려주세요')">
            당근 마켓에서의 거래 매너를 알려주세요
        </button>
        <button onclick="sendFaq('당근 마켓에서의 운영 정책을 알려주세요')">
            당근 마켓에서의 운영 정책을 알려주세요
        </button>
    `;
    container.appendChild(faqDiv);
}

function resetBotChat() {
    if (!botSocket || botSocket.readyState !== WebSocket.OPEN) {
        alert("챗봇 연결이 끊겼습니다.");
        return;
    }
    botSocket.send("__RESET_CHAT__");  // ✅ 서버에 대화 초기화 요청
}

function addResetChatButton(container) {
    // 기존 버튼 있으면 제거
    const oldReset = container.querySelector(".reset-chat-btn");
    if (oldReset) oldReset.remove();

    const resetDiv = document.createElement("div");
    resetDiv.className = "reset-chat-btn";
    resetDiv.innerHTML = `
        <button id="resetChatBtn" class="reset-btn">
            🔄 대화 종료 / 초기화
        </button>
    `;
    container.appendChild(resetDiv);

    // 버튼 클릭 이벤트 등록
    document.getElementById("resetChatBtn").addEventListener("click", () => {
        if (!botSocket || botSocket.readyState !== WebSocket.OPEN) {
            alert("⚠️ 챗봇 연결이 끊겼습니다. 새로고침 후 다시 시도해주세요.");
            return;
        }

        if (confirm("정말 대화를 초기화할까요?")) {
            botSocket.send("__RESET_CHAT__");
        }
    });
}
