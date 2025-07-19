// 순수 WebSocket을 이용한 실시간 채팅 기능
document.addEventListener("DOMContentLoaded", function() {
    // DOM 요소들
    const messageInput = document.getElementById("messageInput");
    const sendBtn = document.getElementById("sendBtn");
    const messagesContainer = document.getElementById("messages-container");
    const conversation = document.getElementById("chat-conversation");
    const placeholder = document.getElementById("conversation-placeholder");
    const messageForm = document.getElementById("message-form");
    const charCount = document.getElementById("char-count");

    // 현재 상태
    let currentRoomId = selectedRoomId;
    let currentAssistantId = '';
    let socket = null;
    let isConnected = false;
    window.isBotChat = false;

    // 초기화
    init();

    function init() {
        // WebSocket 연결
        connectWebSocket();

        // 이벤트 리스너 등록
        setupEventListeners();

        // UI 이벤트 리스너 설정
        setupUIEventListeners();

        // 기존 메시지 읽음 처리
        if (currentRoomId) {
            markAsRead(currentRoomId);
        }

        // 페이지 로드 시 스크롤을 맨 아래로
        scrollToBottom();

        console.log("Chat system initialized");
    }

    function setupEventListeners() {
        // 채팅방 선택
        document.querySelectorAll(".chat-item").forEach(item => {
            item.addEventListener("click", function() {
                const roomId = this.getAttribute("data-room-id");
                const partnerName = this.getAttribute("data-partner-name");

                console.log("✅ chat-item 클릭됨!", roomId, partnerName);

                // 챗봇일 경우 일반 채팅방 로직을 건너뛰고 openBotChat 실행
                if (roomId === "BOT_CHAT") {
                    console.log("🤖 챗봇 클릭 → openBotChat 실행");
                    openBotChat();
                } else {
                    console.log("💬 일반 채팅 클릭 → selectChatRoom 실행");
                    selectChatRoom(roomId, partnerName);
                }
            });
        });


        // 메시지 전송 (폼 제출)
        if (messageForm) {
            messageForm.addEventListener("submit", function(e) {
                e.preventDefault();
                sendMessage();
            });
        }

        // 엔터키로 메시지 전송
        if (messageInput) {
            messageInput.addEventListener("keydown", function(e) {
                if (e.key === "Enter" && !e.shiftKey) {
                    e.preventDefault();
                    sendMessage();
                } else if (e.key === "Enter" && e.shiftKey) {
                    // Shift+Enter는 줄바꿈 허용
                    return true;
                }
            });

            // 문자수 카운터
            messageInput.addEventListener("input", function() {
                const currentLength = this.value.length;
                if (charCount) {
                    charCount.textContent = currentLength;
                }

                // 최대 글자수 체크
                if (currentLength >= 1000) {
                    this.value = this.value.substring(0, 1000);
                    charCount.textContent = "1000";
                }

                // 전송 버튼 활성화/비활성화
                if (sendBtn) {
                    sendBtn.disabled = currentLength === 0;
                }
            });
        }

        // 읽지 않은 토글 스위치
        const toggleSwitch = document.getElementById("toggleUnreadSwitch");
        if (toggleSwitch) {
            toggleSwitch.addEventListener("change", function() {
                toggleUnreadChats(this.checked);
            });
        }
    }

    // =====================================================
    // UI 이벤트 리스너 설정
    // =====================================================

    function setupUIEventListeners() {
        // 거래완료 버튼 이벤트
        setupCompleteTradeButton();

        // 채팅방 설정 드롭다운 이벤트
        setupChatSettingsDropdown();

        // 모달 이벤트 설정
        setupModalEvents();

        // 외부 클릭 시 드롭다운 닫기
        setupOutsideClickHandlers();
    }

    // 거래완료 버튼 기능
    function setupCompleteTradeButton() {
        const completeTradeBtn = document.getElementById('completeTradeBtn');

        if (completeTradeBtn) {
            completeTradeBtn.addEventListener('click', function() {
                showCompleteTradeModal();
            });
        }
    }

    function showCompleteTradeModal() {
        const modal = document.getElementById('completeTradeModal');
        const modalProductImage = document.getElementById('modalProductImage');
        const modalProductTitle = document.getElementById('modalProductTitle');
        const modalProductPrice = document.getElementById('modalProductPrice');

        // 현재 상품 정보로 모달 업데이트
        const productImage = document.getElementById('product-image');
        const productTitle = document.getElementById('product-title');
        const productPrice = document.getElementById('product-price');

        if (modalProductImage && productImage) {
            modalProductImage.src = productImage.src;
        }
        if (modalProductTitle && productTitle) {
            modalProductTitle.textContent = productTitle.textContent;
        }
        if (modalProductPrice && productPrice) {
            modalProductPrice.textContent = productPrice.textContent;
        }

        modal.style.display = 'flex';
    }

    function hideCompleteTradeModal() {
        const modal = document.getElementById('completeTradeModal');
        modal.style.display = 'none';
    }

    function completeTrade() {
        if (!currentRoomId) {
            showErrorMessage('채팅방 정보를 찾을 수 없습니다.');
            return;
        }

        // 로딩 표시
        showLoadingMessage('거래 완료 처리 중...');

        fetch(`/api/chats/${currentRoomId}/complete`, {
            method: 'POST',
            headers: createHeaders(),
            credentials: 'include'
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                hideLoadingMessage();

                if (data.success) {
                    hideCompleteTradeModal();
                    showSuccessMessage('거래가 완료되었습니다!');

                    // UI 업데이트 - 거래완료 버튼 비활성화
                    updateUIForCompletedTrade();

                } else {
                    showErrorMessage(data.message || '거래 완료 처리에 실패했습니다.');
                }
            })
            .catch(error => {
                hideLoadingMessage();
                console.error('거래 완료 처리 실패:', error);
                showErrorMessage('거래 완료 처리 중 오류가 발생했습니다.');
            });
    }

    function updateUIForCompletedTrade() {
        const completeBtn = document.getElementById('completeTradeBtn');
        if (completeBtn) {
            completeBtn.innerHTML = '<span class="btn-icon">✓</span><span class="btn-text">거래완료됨</span>';
            completeBtn.disabled = true;
            completeBtn.style.background = '#28a745';
            completeBtn.style.cursor = 'not-allowed';
        }
    }

    // 채팅방 설정 드롭다운 기능
    function setupChatSettingsDropdown() {
        const settingsBtn = document.getElementById('chatSettingsBtn');
        const dropdown = document.getElementById('settingsDropdown');

        if (settingsBtn && dropdown) {
            settingsBtn.addEventListener('click', function(e) {
                e.stopPropagation();
                toggleSettingsDropdown();
            });

            // 드롭다운 메뉴 아이템 클릭 이벤트
            dropdown.addEventListener('click', function(e) {
                const menuItem = e.target.closest('.settings-menu-item');
                if (menuItem) {
                    const action = menuItem.getAttribute('data-action');
                    handleSettingsAction(action);
                    hideSettingsDropdown();
                }
            });
        }
    }

    function toggleSettingsDropdown() {
        const dropdown = document.getElementById('settingsDropdown');
        if (dropdown) {
            dropdown.classList.toggle('show');
        }
    }

    function hideSettingsDropdown() {
        const dropdown = document.getElementById('settingsDropdown');
        if (dropdown) {
            dropdown.classList.remove('show');
        }
    }

    function handleSettingsAction(action) {
        switch (action) {
            case 'block':
                showToastMessage('차단 기능은 준비 중입니다.', 'info');
                break;
            case 'report':
                showToastMessage('신고 기능은 준비 중입니다.', 'info');
                break;
            case 'leave':
                showLeaveChatModal();
                break;
            default:
                console.warn('Unknown action:', action);
        }
    }

    // 채팅방 나가기 기능
    function showLeaveChatModal() {
        const modal = document.getElementById('leaveChatModal');
        modal.style.display = 'flex';
    }

    function hideLeaveChatModal() {
        const modal = document.getElementById('leaveChatModal');
        modal.style.display = 'none';
    }

    function leaveChatRoom() {
        if (!currentRoomId) {
            showErrorMessage('채팅방 정보를 찾을 수 없습니다.');
            return;
        }

        // 로딩 표시
        showLoadingMessage('채팅방에서 나가는 중...');

        fetch(`/api/chats/${currentRoomId}/leave`, {
            method: 'DELETE',
            headers: createHeaders(),
            credentials: 'include'
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                hideLoadingMessage();

                if (data.success) {
                    hideLeaveChatModal();
                    showSuccessMessage(data.message);

                    // WebSocket 연결 해제
                    if (socket && isConnected) {
                        socket.close();
                    }

                    // 페이지 리다이렉트
                    setTimeout(() => {
                        window.location.href = data.redirectUrl || '/chats';
                    }, 1500);

                } else {
                    showErrorMessage(data.message || '채팅방 나가기에 실패했습니다.');
                }
            })
            .catch(error => {
                hideLoadingMessage();
                console.error('채팅방 나가기 실패:', error);
                showErrorMessage('채팅방 나가기 중 오류가 발생했습니다.');
            });
    }

    // 모달 이벤트 설정
    function setupModalEvents() {
        // 거래완료 모달 이벤트
        const cancelCompleteBtn = document.getElementById('cancelCompleteBtn');
        const confirmCompleteBtn = document.getElementById('confirmCompleteBtn');
        const completeModal = document.getElementById('completeTradeModal');

        if (cancelCompleteBtn) {
            cancelCompleteBtn.addEventListener('click', hideCompleteTradeModal);
        }

        if (confirmCompleteBtn) {
            confirmCompleteBtn.addEventListener('click', function() {
                completeTrade();
            });
        }

        if (completeModal) {
            completeModal.addEventListener('click', function(e) {
                if (e.target === completeModal) {
                    hideCompleteTradeModal();
                }
            });
        }

        // 채팅방 나가기 모달 이벤트
        const cancelLeaveBtn = document.getElementById('cancelLeaveBtn');
        const confirmLeaveBtn = document.getElementById('confirmLeaveBtn');
        const leaveModal = document.getElementById('leaveChatModal');

        if (cancelLeaveBtn) {
            cancelLeaveBtn.addEventListener('click', hideLeaveChatModal);
        }

        if (confirmLeaveBtn) {
            confirmLeaveBtn.addEventListener('click', function() {
                leaveChatRoom();
            });
        }

        if (leaveModal) {
            leaveModal.addEventListener('click', function(e) {
                if (e.target === leaveModal) {
                    hideLeaveChatModal();
                }
            });
        }

        // ESC 키로 모달 닫기
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape') {
                hideCompleteTradeModal();
                hideLeaveChatModal();
                hideSettingsDropdown();
            }
        });
    }

    // 외부 클릭 핸들러
    function setupOutsideClickHandlers() {
        document.addEventListener('click', function(e) {
            // 설정 드롭다운 외부 클릭 시 닫기
            const settingsBtn = document.getElementById('chatSettingsBtn');
            const dropdown = document.getElementById('settingsDropdown');

            if (settingsBtn && dropdown && !settingsBtn.contains(e.target) && !dropdown.contains(e.target)) {
                hideSettingsDropdown();
            }
        });
    }

    // =====================================================
    // 기존 채팅 기능들
    // =====================================================

    // WebSocket 연결
    function connectWebSocket() {
        if (!currentUserId) {
            console.warn("User not logged in, WebSocket connection skipped");
            return;
        }

        try {
            // 순수 WebSocket 연결 (STOMP 없음)
            const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
            const wsUrl = `${protocol}//${window.location.host}/ws/chat`;

            socket = new WebSocket(wsUrl);

            socket.onopen = function(event) {
                console.log('WebSocket Connected:', event);
                isConnected = true;
                updateOnlineStatus(true);
            };

            socket.onmessage = function(event) {
                try {
                    const messageData = JSON.parse(event.data);
                    console.log('Received message:', messageData);
                    handleIncomingMessage(messageData);
                } catch (error) {
                    console.error('Failed to parse message:', error);
                }
            };

            socket.onclose = function(event) {
                console.log('WebSocket Disconnected:', event);
                isConnected = false;
                updateOnlineStatus(false);

                // 재연결 시도 (5초 후)
                setTimeout(function() {
                    console.log('Attempting to reconnect WebSocket...');
                    connectWebSocket();
                }, 5000);
            };

            socket.onerror = function(error) {
                console.error('WebSocket Error:', error);
                isConnected = false;
            };

        } catch (error) {
            console.error('WebSocket initialization error:', error);
        }
    }

    // 개선된 헤더 생성 함수
    function createHeaders() {
        const headers = {
            'Content-Type': 'application/json'
        };

        // CSRF 헤더가 존재하고 유효한 경우에만 추가
        if (csrfHeader && csrfToken && typeof csrfHeader === 'string' && csrfHeader.trim()) {
            headers[csrfHeader] = csrfToken;
        }

        return headers;
    }

    // 채팅방 선택
    function selectChatRoom(roomId, partnerName) {
        isBotChat = false; // 일반 채팅 모드
        toggleChatRoomButtons(true); // 오른쪽 버튼 표시

        if (roomId === currentRoomId) {
            return; // 이미 선택된 채팅방
        }

        currentRoomId = roomId;
        currentAssistantId = partnerName; // 임시로 partnerName을 assistantId로 사용

        // UI 업데이트
        updateChatRoomSelection(roomId);

        // 채팅방 데이터 로드
        loadChatRoomData(roomId);

        // 읽음 처리
        markAsRead(roomId);

        console.log('Selected chat room:', roomId);
    }

    // 채팅방 UI 선택 상태 업데이트
    function updateChatRoomSelection(roomId) {
        // 모든 채팅 아이템에서 선택 상태 제거
        document.querySelectorAll(".chat-item").forEach(item => {
            item.classList.remove("selected");
        });

        // 선택된 채팅방에 선택 상태 추가
        const selectedItem = document.querySelector(`[data-room-id="${roomId}"]`);
        if (selectedItem) {
            selectedItem.classList.add("selected");

            // 상대방 이름 가져오기
            const partnerName = selectedItem.getAttribute("data-partner-name");
            currentAssistantId = partnerName;
        }

        // 일반 채팅이면 partner-info UI 복구 (AI 챗봇일 땐 유지)
        if (roomId !== "BOT_CHAT") {
            document.getElementById("partner-info").innerHTML = `
                <span class="partner-name" id="partner-name"></span>
            `;
        }

        // 대화 영역 표시
        if (conversation) {
            conversation.style.display = "flex";
        }
        if (placeholder) {
            placeholder.style.display = "none";
        }

        // UI 이벤트 리스너 재설정
        setupUIEventListeners();

        // 폼 필드 업데이트
        updateFormFields();
    }

    // 폼 필드 업데이트
    function updateFormFields() {
        const roomIdInput = document.getElementById("roomId");
        const assistantIdInput = document.getElementById("assistantId");

        if (roomIdInput) {
            roomIdInput.value = currentRoomId || '';
        }
        if (assistantIdInput) {
            assistantIdInput.value = currentAssistantId || '';
        }
    }

    // 채팅방 데이터 로드
    function loadChatRoomData(roomId) {
        const url = `/api/chats/${roomId}`;

        fetch(url, {
            method: 'GET',
            headers: createHeaders(),
            credentials: 'include'
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                updateChatRoomUI(data);
                updateMessagesUI(data.messages || []);
                scrollToBottom();
            })
            .catch(error => {
                console.error('Failed to load chat room data:', error);
                showErrorMessage('채팅방 데이터를 불러오는데 실패했습니다.');
            });
    }

    // 채팅방 UI 업데이트
    function updateChatRoomUI(chatRoomData) {
        console.log('Received chat room data:', chatRoomData);

        // 상대방 이름 업데이트
        const partnerNameElement = document.getElementById("partner-name");
        if (partnerNameElement && chatRoomData.otherUserName) {
            partnerNameElement.textContent = chatRoomData.otherUserName;
            currentAssistantId = chatRoomData.otherUserName;
        }

        // 상품 정보 업데이트
        const productImage = document.getElementById("product-image");
        const productTitle = document.getElementById("product-title");
        const productPrice = document.getElementById("product-price");

        if (productImage && chatRoomData.productImageUrl) {
            productImage.src = chatRoomData.productImageUrl;
            productImage.onerror = () => { productImage.src = '/images/mascot.png'; };
        }
        if (productTitle && chatRoomData.productTitle) {
            productTitle.textContent = chatRoomData.productTitle;
        }
        if (productPrice && chatRoomData.productPrice) {
            productPrice.textContent = formatPrice(chatRoomData.productPrice);
        }

        // 거래 상태에 따른 UI 업데이트
        if (chatRoomData.isCompleted) {
            updateUIForCompletedTrade();
        }

        // 폼 필드 업데이트
        updateFormFields();
    }

    // 메시지 UI 업데이트
    function updateMessagesUI(messages) {
        if (!messagesContainer) return;

        // 기존 메시지 제거 (no-messages 플레이스홀더 포함)
        messagesContainer.innerHTML = '';

        if (!messages || messages.length === 0) {
            showNoMessagesPlaceholder();
            return;
        }

        // 메시지들 추가
        messages.forEach(message => {
            appendMessageToUI(message);
        });
    }

    // 메시지 전송
    function sendMessage() {
        console.log("📝 sendMessage 실행됨! isBotChat =", window.isBotChat);

        const content = messageInput?.value.trim();
        if (!content) return;

        // ✅ 챗봇 모드
        if (isBotChat) {
            console.log("🤖 챗봇 모드에서 전송");

            if (!botSocket || botSocket.readyState !== WebSocket.OPEN) {
                showErrorMessage("챗봇 연결이 끊겼습니다. 새로고침 후 다시 시도해주세요.");
                return;
            }

            // 1) 내 질문을 UI에 먼저 표시
            addBotMessage(content);

            // 2) 챗봇 WebSocket으로 질문 전송
            botSocket.send(content);

            // 3) 입력창 초기화
            messageInput.value = "";
            if (charCount) charCount.textContent = "0";

            return; // ✅ 챗봇 모드면 일반 채팅 로직 스킵
        }

        // ✅ 일반 채팅 모드
        if (!currentRoomId) {
            console.warn("💬 일반 채팅방이 선택되지 않음 → 전송 불가");
            return;
        }

        if (!isConnected || !socket || socket.readyState !== WebSocket.OPEN) {
            showErrorMessage("연결이 끊어졌습니다. 페이지를 새로고침해주세요.");
            return;
        }

        // 전송 버튼 비활성화
        if (sendBtn) {
            sendBtn.disabled = true;
            sendBtn.textContent = "전송중...";
        }

        const messageData = {
            chatRoomId: parseInt(currentRoomId),
            userId: currentUserId,
            assistantId: currentAssistantId,
            content: content
        };

        try {
            // WebSocket으로 메시지 전송
            socket.send(JSON.stringify(messageData));

            // 입력창 초기화
            messageInput.value = "";
            if (charCount) charCount.textContent = "0";

            // 낙관적 UI 업데이트
            const optimisticMessage = {
                ...messageData,
                sentAt: formatCurrentTime()
            };
            appendMessageToUI(optimisticMessage);
            scrollToBottom();

        } catch (error) {
            console.error("Failed to send message:", error);
            showErrorMessage("메시지 전송에 실패했습니다.");
        } finally {
            if (sendBtn) {
                sendBtn.disabled = false;
                sendBtn.textContent = "전송";
            }
        }
    }

    // 수신된 메시지 처리
    function handleIncomingMessage(messageData) {
        console.log('Handling incoming message:', messageData);

        // 메시지 데이터 유효성 검사
        if (!messageData.chatRoomId || !messageData.userId || !messageData.content) {
            console.error('Invalid message data:', messageData);
            return;
        }

        // 현재 채팅방의 메시지가 아니면 리스트만 업데이트
        if (messageData.chatRoomId != currentRoomId) {
            updateChatListPreview(messageData);
            return;
        }

        // 내가 보낸 메시지는 이미 UI에 추가되어 있으므로 중복 방지
        if (messageData.userId === currentUserId) {
            return;
        }

        // 상대방 메시지 추가
        appendMessageToUI(messageData);
        scrollToBottom();

        // 읽음 처리
        markAsRead(currentRoomId);
    }

    // UI에 메시지 추가
    function appendMessageToUI(message) {
        if (!messagesContainer) return;

        const messageWrapper = document.createElement('div');
        messageWrapper.className = 'message-wrapper';

        const messageDiv = document.createElement('div');
        messageDiv.className = 'message';

        const isMyMessage = message.userId === currentUserId;
        const timeStr = message.sentAt || formatCurrentTime();

        if (isMyMessage) {
            // 내 메시지 (오른쪽)
            messageDiv.classList.add('sent');
            messageDiv.innerHTML = `
                <div class="message-time">${timeStr}</div>
                <div class="message-content">${escapeHtml(message.content)}</div>
            `;
        } else {
            // 상대방 메시지 (왼쪽)
            messageDiv.classList.add('received');
            messageDiv.innerHTML = `
                <div class="message-content">${escapeHtml(message.content)}</div>
                <div class="message-time">${timeStr}</div>
            `;
        }

        messageWrapper.appendChild(messageDiv);
        messagesContainer.appendChild(messageWrapper);
    }

    // 채팅 리스트 미리보기 업데이트
    function updateChatListPreview(messageData) {
        const chatItem = document.querySelector(`[data-room-id="${messageData.chatRoomId}"]`);
        if (chatItem) {
            const previewElement = chatItem.querySelector('.chat-preview');
            const timeElement = chatItem.querySelector('.chat-time');
            const unreadElement = chatItem.querySelector('.unread-count');

            if (previewElement) {
                previewElement.textContent = messageData.content;
            }
            if (timeElement) {
                timeElement.textContent = formatCurrentTime();
            }

            // 읽지 않은 메시지 카운트 업데이트
            if (messageData.userId !== currentUserId) {
                if (!unreadElement) {
                    const newUnreadElement = document.createElement('div');
                    newUnreadElement.className = 'unread-count';
                    newUnreadElement.textContent = '1';
                    chatItem.querySelector('.chat-meta').appendChild(newUnreadElement);
                } else {
                    const currentCount = parseInt(unreadElement.textContent) || 0;
                    unreadElement.textContent = currentCount + 1;
                }
                chatItem.classList.add('has-unread');
            }
        }
    }

    // 읽음 처리
    function markAsRead(roomId) {
        if (!roomId) return;

        fetch(`/api/chats/${roomId}/read`, {
            method: 'POST',
            headers: createHeaders(),
            credentials: 'include'
        })
            .then(response => {
                if (response.ok) {
                    // 읽지 않은 메시지 카운트 제거
                    const chatItem = document.querySelector(`[data-room-id="${roomId}"]`);
                    if (chatItem) {
                        const unreadElement = chatItem.querySelector('.unread-count');
                        if (unreadElement) {
                            unreadElement.remove();
                        }
                        chatItem.classList.remove('has-unread');
                    }
                }
            })
            .catch(error => {
                console.error('Failed to mark messages as read:', error);
            });
    }

    // 읽지 않은 채팅 토글
    function toggleUnreadChats(showUnreadOnly) {
        const allChats = document.getElementById("all-chats-list");
        const unreadChats = document.getElementById("unread-chats-list");

        if (showUnreadOnly) {
            if (allChats) allChats.style.display = "none";
            if (unreadChats) unreadChats.style.display = "block";
        } else {
            if (allChats) allChats.style.display = "block";
            if (unreadChats) unreadChats.style.display = "none";
        }
    }

    // 온라인 상태 업데이트
    function updateOnlineStatus(isOnline) {
        const statusElement = document.getElementById("online-status");
        if (statusElement) {
            statusElement.className = `online-status ${isOnline ? '' : 'offline'}`;
        }
    }

    // =====================================================
    // 유틸리티 함수들
    // =====================================================

    function showLoadingMessage(message) {
        // 기존 로딩 오버레이 제거
        const existingOverlay = document.getElementById('loading-overlay');
        if (existingOverlay) {
            existingOverlay.remove();
        }

        const loading = document.createElement('div');
        loading.id = 'loading-overlay';
        loading.innerHTML = `
            <div class="loading-content">
                <div class="spinner"></div>
                <p>${message}</p>
            </div>
        `;
        loading.style.cssText = `
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.5);
            display: flex;
            justify-content: center;
            align-items: center;
            z-index: 3000;
        `;

        document.body.appendChild(loading);
    }

    function hideLoadingMessage() {
        const loading = document.getElementById('loading-overlay');
        if (loading) {
            loading.remove();
        }
    }

    function showSuccessMessage(message) {
        showToastMessage(message, 'success');
    }

    function showErrorMessage(message) {
        showToastMessage(message, 'error');
    }

    function showToastMessage(message, type = 'info') {
        // 기존 토스트 제거
        const existingToasts = document.querySelectorAll('.toast-message');
        existingToasts.forEach(toast => toast.remove());

        const toast = document.createElement('div');
        toast.className = `toast-message toast-${type}`;
        toast.textContent = message;

        const backgroundColor = {
            success: '#28a745',
            error: '#dc3545',
            warning: '#ffc107',
            info: '#17a2b8'
        };

        toast.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            background-color: ${backgroundColor[type] || backgroundColor.info};
            color: white;
            padding: 12px 20px;
            border-radius: 6px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            z-index: 4000;
            font-size: 14px;
            max-width: 300px;
            animation: slideInRight 0.3s ease-out;
            cursor: pointer;
        `;

        document.body.appendChild(toast);

        // 3초 후 자동 제거
        setTimeout(() => {
            toast.style.animation = 'slideOutRight 0.3s ease-out forwards';
            setTimeout(() => {
                if (toast.parentNode) {
                    toast.remove();
                }
            }, 300);
        }, 3000);

        // 클릭 시 즉시 제거
        toast.addEventListener('click', () => {
            toast.style.animation = 'slideOutRight 0.3s ease-out forwards';
            setTimeout(() => {
                if (toast.parentNode) {
                    toast.remove();
                }
            }, 300);
        });
    }

    function scrollToBottom() {
        if (messagesContainer) {
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        }
    }

    function formatCurrentTime() {
        const now = new Date();
        return now.toLocaleTimeString('ko-KR', {
            hour: 'numeric',
            minute: '2-digit',
            hour12: true
        });
    }

    function formatPrice(price) {
        if (typeof price === 'number') {
            return price.toLocaleString('ko-KR') + '원';
        }
        return price;
    }

    function escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    function showNoMessagesPlaceholder() {
        messagesContainer.innerHTML = `
            <div class="no-messages">
                <div class="no-messages-icon">💭</div>
                <p>아직 메시지가 없습니다.</p>
                <small>첫 메시지를 보내보세요!</small>
            </div>
        `;
    }

    // 채팅방 상단 헤더 챗봇 버튼 숨기기
    window.toggleChatRoomButtons = function(show) {
        // 거래완료 버튼
        const completeTradeBtn = document.getElementById('completeTradeBtn');
        if (completeTradeBtn) {
            completeTradeBtn.style.display = show ? 'inline-flex' : 'none';
        }

        // 기존 설정 버튼
        const settingsBtn = document.getElementById('chatSettingsBtn');
        if (settingsBtn) {
            settingsBtn.style.display = show ? 'inline-flex' : 'none';
        }

        // 더보기 버튼
        const moreOptionsBtn = document.querySelector('.chat-more-options');
        if (moreOptionsBtn) {
            moreOptionsBtn.style.display = show ? 'inline-flex' : 'none';
        }

        // 빠른 채팅방 나가기 버튼
        const quickLeaveBtn = document.getElementById('quickLeaveBtn');
        if (quickLeaveBtn) {
            quickLeaveBtn.style.display = show ? 'inline-flex' : 'none';
        }
    };


    // 페이지 언로드 시 WebSocket 연결 해제
    window.addEventListener('beforeunload', function() {
        if (socket && isConnected) {
            updateOnlineStatus(false);
            socket.close();
        }
    });

    // 페이지 포커스 시 읽음 처리
    window.addEventListener('focus', function() {
        if (currentRoomId) {
            markAsRead(currentRoomId);
        }
    });

    console.log('Chat WebSocket system loaded successfully');
});