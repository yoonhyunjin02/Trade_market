function openBotChat() {
    document.getElementById("conversation-placeholder").style.display = "none";
    document.getElementById("chat-conversation").style.display = "flex";

    document.getElementById("partner-info").innerHTML = `
        <div class="bot-header-icon">
            <img src="/images/icons/chat-bot.svg" alt="AI 챗봇">
        </div>
        <span class="partner-name">AI 챗봇</span>
    `;

    const productInfoCard = document.querySelector(".product-info-card");
    if (productInfoCard) productInfoCard.style.display = "none";

    const now = new Date();
    const timeString = now.toLocaleTimeString("ko-KR", {
        hour: "numeric",
        minute: "2-digit",
        hour12: true
    });

    const userName = currentUserName || "사용자";
    const messagesContainer = document.getElementById("messages-container");
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

    // ✅ 첫 로딩 시 FAQ 버튼 추가
    addFaqButtons(messagesContainer);
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

    // ✅ 질문 말풍선 위치로 스크롤 이동
    msgDiv.scrollIntoView({ behavior: "smooth", block: "center" });
}

function fetchBotAnswer(question) {
    let answer = "";

    if (question.includes("금지")) {
        answer = `📌 <strong>당근 중고거래 금지물품</strong><br>
        당근은 현행 법령을 위반하는 물품의 거래는 물론, 당근에서 거래가 부적절하다고 판단되는 물품에 대해 운영정책에 따라 금지하고 있어요.
        또한 판매자격을 갖춘 경우라도 개인 간 거래를 지향하는 당근 중고거래 서비스에서는 해당 물품을 판매할 수 없어요.<br><br>

        🚫 <strong>거래 금지 물품</strong><br>
        - 생명이 있는 동물 (식물 제외, 곤충·관상어 포함)<br>
        - 개인정보: 신분증, 통장, 신용정보<br>
        - 상표권 침해 물품: 가품, 이미지/위조품<br>
        - 청소년유해물품: 주류, 담배, 라이터, 마약류<br>
        - 청소년유해매체물: 음란물, 성상품화물, 게임 아이템<br>
        - 의약품/의료기기 (전자체온계, 혈압계는 가능)<br>
        - 콘텐츠/도난 위험물: 불법 복제물, 랜덤박스<br>
        - 무허가·위해 식품 및 화장품<br>
        - 위험물: 농약, 휘발유, 경유, LPG<br>
        - 위험물품: 총포·도검·전기충격기<br>
        - 군·경찰/관공서 물품: 군복·경찰복·제복<br>
        - 정부지원 물품: 상품권/바우처/온누리상품권 등<br>
        - 통신사 데이터, 렌탈물품, 도난 신고물품<br>
        - 소비기한 지난 물품, 정보 확인 불가 물품(랜덤박스)<br><br>

        ⚠️ <strong>운영정책상 금지 행위</strong><br>
        - 사회 통념상 용인이 되지 않는 행위<br>
        - 암표 매매 행위 (철도 승차권 반복 판매 등)<br>
        - 불법 유사 의료 행위 (반영구 화장, 문신 등)<br>
        - 등록되지 않은 광고 행위<br>
        - 조건 있는 무료나눔 행위<br><br>

        ✅ 위반 시 최대 365일 이용 제한 및 법적 책임이 따를 수 있어요.
        `;
    } else if (question.includes("매너")) {
        answer = `🧡 <strong>당근 거래 매너</strong><br>
        당근은 이웃과 따뜻한 경험을 나누는 공간이에요. 서로 배려하며 거래해주세요!<br><br>

        ✅ <strong>기본 매너</strong><br>
        - 서로 존중하며 정중하게 대화해요.<br>
        - 욕설, 비방, 혐오 표현은 금지!<br>
        - 시간 약속 꼭 지켜주세요 (잠수는 절대 금지)<br>
        - 늦은 시간, 특히 새벽에는 채팅을 자제해주세요.<br>
        - 택배 거래는 부득이한 경우만 요청하고, 가급적 직접 만나 거래하세요.<br><br>

        ✅ <strong>구매자 매너</strong><br>
        - 충분히 고민한 후 거래 약속을 잡아요.<br>
        - 질문 전 판매글을 꼼꼼히 읽어주세요.<br>
        - 지나치게 가격을 깎는 건 삼가주세요.<br>
        - 직접 만나 거래할 땐 안전한 장소를 선택하세요.<br>
        - 물품 금액에 맞게 현금을 미리 준비해주세요.<br>
        - 무료나눔을 받았다면 감사 인사를 꼭 남겨주세요.<br><br>

        ✅ <strong>판매자 매너</strong><br>
        - 직접 촬영한 사진으로 판매글을 작성해주세요.<br>
        - 물품 설명은 솔직하고 구체적으로 작성해주세요.<br>
        - 사용감이 있어도 깨끗하게 세탁·정리 후 거래하면 서로 기분이 좋아요.<br>
        - 약속을 지키지 못한다면 미리 연락주세요.<br><br>

        모두가 매너를 지킬 때 따뜻하고 안전한 거래가 만들어집니다 😊
        `;
    } else if (question.includes("운영 정책")) {
        answer = `📌 <strong>당근 중고거래 운영정책</strong><br>
        당근은 <strong>신뢰·존중·윤리</strong>를 바탕으로 안전하고 따뜻한 지역 사회를 만들기 위해 운영돼요.<br><br>

        ✅ <strong>기본 매너</strong><br>
        - 존댓말로 예의 있게 대화하기<br>
        - 시간 약속 꼭 지키기 (잠수 금지)<br>
        - 늦은 시간 채팅 자제 (특히 새벽 시간)<br>
        - 택배보다는 직접 만나 거래 권장<br>
        - 이웃과 거래는 만 14세 이상만 가능<br><br>

        ✅ <strong>구매자 매너</strong><br>
        - 판매글 꼼꼼히 읽고 질문하기<br>
        - 충분히 고민 후 거래 약속 잡기<br>
        - 판매자의 가격 책정을 존중하기<br>
        - 지나친 가격 흥정은 지양하기<br>
        - 무료 나눔 받으면 감사 인사하기<br><br>

        ✅ <strong>판매자 매너</strong><br>
        - 직접 촬영한 사진으로 판매글 작성<br>
        - 주요 하자, 물품 상태 정확히 기재<br>
        - 거래 전 물품 청결하게 관리하기<br>
        - 거래 약속 시 ‘예약중’으로 상태 변경<br>
        - 재판매 시 구매가보다 비싸게 판매 금지<br><br>

        ✅ <strong>거래 시 주의</strong><br>
        - 밝고 안전한 장소에서 만나 거래하기<br>
        - 집 앞이라도 집 안까지 들어가진 않기<br>
        - 고가 물품일수록 더 신중하게 거래하기<br>
        - 사용감으로 인한 환불은 어려움<br><br>

        ✅ <strong>운영정책 위반 시</strong><br>
        - 게시글 숨김, 경고·주의 메시지 발송<br>
        - 최대 365일 이용 제한 또는 영구 정지<br>
        - 심각한 위반 시 법적 조치 가능<br><br>

        당근은 <strong>따뜻하고 안전한 거래 문화</strong>를 위해 운영정책을 지속적으로 개선하고 있어요 😊
        `;
    } else {
        answer = "조금 더 구체적으로 물어봐주시면 더 정확히 답변할 수 있어요!";
    }

        const now = new Date();
        const timeString = now.toLocaleTimeString("ko-KR", {
            hour: "numeric",
            minute: "2-digit",
            hour12: true
        });

        const msgDiv = document.createElement("div");
        msgDiv.className = "message received";
        msgDiv.innerHTML = `<div class="message-content">${answer}</div><div class="message-time">${timeString}</div>`;

        const container = document.getElementById("messages-container");
        container.appendChild(msgDiv);

        // ✅ 답변 출력 후 다시 FAQ 버튼 추가
        addFaqButtons(container);

        // ✅ 답변 말풍선 위치로 스크롤 이동
        msgDiv.scrollIntoView({ behavior: "smooth", block: "center" });
    }

    function addFaqButtons(container) {
        const faqDiv = document.createElement("div");
        faqDiv.className = "faq-buttons";
        faqDiv.innerHTML = `
            <button onclick="sendFaq('법적으로 중고 거래를 금지하는 품목이 있나요?')">법적으로 중고 거래를 금지하는 품목이 있나요?</button>
            <button onclick="sendFaq('당근 마켓에서의 거래 매너를 알려주세요')">당근 마켓에서의 거래 매너를 알려주세요</button>
            <button onclick="sendFaq('당근 마켓에서의 운영 정책을 알려주세요')">당근 마켓에서의 운영 정책을 알려주세요</button>
        `;
        container.appendChild(faqDiv);
    }