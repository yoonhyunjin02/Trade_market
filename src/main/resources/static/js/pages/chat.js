document.addEventListener('DOMContentLoaded', function() {
    const chatRooms = document.getElementById('chatRooms');
    const messages = document.getElementById('messages');
    const productSummary = document.getElementById('productSummary');
    const sendBtn = document.getElementById('sendBtn');
    const messageInput = document.getElementById('messageInput');

    // 채팅방 목록 불러오기
    fetch('/chats')
        .then(response => response.json())
        .then(data => {
            data.forEach(room => {
                const div = document.createElement('div');
                div.className = 'room';
                div.textContent = room.userName + ' - ' + room.lastMessage;
                div.onclick = () => enterChatRoom(room.id);
                chatRooms.appendChild(div);
            });
        });

    // 채팅방 들어가기
    function enterChatRoom(chatRoomId) {
        fetch(`/chats/${chatRoomId}`)
            .then(response => response.json())
            .then(data => {
                messages.innerHTML = '';
                data.messages.forEach(msg => {
                    const div = document.createElement('div');
                    div.className = 'message' + (msg.sender === '나' ? ' me' : '');
                    div.innerHTML = `<span>${msg.message}</span> <small>${msg.time}</small>`;
                    messages.appendChild(div);
                });

                productSummary.innerHTML = `
                    <img src="${data.product.image}">
                    <h3>${data.product.title}</h3>
                    <p>${data.product.price.toLocaleString()}원</p>
                    <button>거래완료</button>
                `;
            });
    }

    // 메시지 전송
    sendBtn.onclick = () => {
        const text = messageInput.value.trim();
        if (!text) return;
        // POST 보내기
        fetch('/messages', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({ message: text })
        }).then(() => {
            const div = document.createElement('div');
            div.className = 'message me';
            div.innerHTML = `<span>${text}</span> <small>지금</small>`;
            messages.appendChild(div);
            messageInput.value = '';
        });
    };
});
