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

    // 초기화
    init();

    function init() {
        // WebSocket 연결
        connectWebSocket();

        // 이벤트 리스너 등록
        setupEventListeners();

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

                selectChatRoom(roomId, partnerName);
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

    // 채팅방 선택
    function selectChatRoom(roomId, partnerName) {
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

        // 대화 영역 표시
        if (conversation) {
            conversation.style.display = "flex";
        }
        if (placeholder) {
            placeholder.style.display = "none";
        }

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
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
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
        }
        if (productTitle && chatRoomData.productTitle) {
            productTitle.textContent = chatRoomData.productTitle;
        }
        if (productPrice && chatRoomData.productPrice) {
            productPrice.textContent = formatPrice(chatRoomData.productPrice) + '원';
        }

        // 폼 필드 업데이트
        updateFormFields();
    }

    // 메시지 UI 업데이트
    function updateMessagesUI(messages) {
        if (!messagesContainer) return;

        // 기존 메시지가 있으면 새로운 메시지만 추가하지 않고 전체 다시 렌더링
        const existingMessages = messagesContainer.querySelectorAll('.message-wrapper');
        if (existingMessages.length === 0) {
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
    }

    // 메시지 전송
    function sendMessage() {
        const content = messageInput?.value.trim();

        if (!content || !currentRoomId) {
            return;
        }

        if (!isConnected || !socket || socket.readyState !== WebSocket.OPEN) {
            showErrorMessage('연결이 끊어졌습니다. 페이지를 새로고침해주세요.');
            return;
        }

        // 전송 버튼 비활성화
        if (sendBtn) {
            sendBtn.disabled = true;
            sendBtn.textContent = '전송중...';
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
            messageInput.value = '';
            if (charCount) {
                charCount.textContent = '0';
            }

            // 내 메시지를 즉시 UI에 추가 (낙관적 업데이트)
            const optimisticMessage = {
                ...messageData,
                sentAt: formatCurrentTime()
            };
            appendMessageToUI(optimisticMessage);
            scrollToBottom();

        } catch (error) {
            console.error('Failed to send message:', error);
            showErrorMessage('메시지 전송에 실패했습니다.');
        } finally {
            // 전송 버튼 복원
            if (sendBtn) {
                sendBtn.disabled = false;
                sendBtn.textContent = '전송';
            }
        }
    }

    // 수신된 메시지 처리
    function handleIncomingMessage(messageData) {
        console.log('Handling incoming message:', messageData);

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
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
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

    // 유틸리티 함수들
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
        return price.toLocaleString('ko-KR');
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

    function showErrorMessage(message) {
        // 간단한 토스트 메시지 표시
        const toast = document.createElement('div');
        toast.className = 'error-toast';
        toast.textContent = message;
        toast.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            background-color: #dc3545;
            color: white;
            padding: 12px 20px;
            border-radius: 6px;
            z-index: 1000;
            animation: slideIn 0.3s ease-out;
        `;

        document.body.appendChild(toast);

        setTimeout(() => {
            toast.remove();
        }, 3000);
    }

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