const form = document.querySelector('#chat-form');
const input = document.querySelector('#message-input');
const messages = document.querySelector('#messages');
const actionButton = document.querySelector('#action-button');

let controller = null;

form.addEventListener('submit', (event) => {
    event.preventDefault();
    if (controller) {
        controller.abort();
        return;
    }
    sendMessage();
});

input.addEventListener('keydown', (event) => {
    if (event.key === 'Enter' && !event.shiftKey && !event.isComposing) {
        event.preventDefault();
        form.requestSubmit();
    }
});

input.addEventListener('input', resizeInput);

async function sendMessage() {
    const text = input.value.trim();
    if (!text) return;

    appendMessage(text, 'user');
    input.value = '';
    resizeInput();

    const responseBubble = appendMessage('', 'assistant');
    responseBubble.classList.add('pending');
    setStreamingState(true);

    controller = new AbortController();
    try {
        const response = await fetch('/chats', {
            method: 'POST',
            headers: { 'Content-Type': 'text/plain;charset=UTF-8' },
            body: text,
            signal: controller.signal
        });

        if (!response.ok) throw new Error(`요청 실패 (${response.status})`);
        if (!response.body) throw new Error('스트리밍 응답을 읽을 수 없습니다.');

        await readEventStream(response.body, (data) => {
            responseBubble.textContent += JSON.parse(data);
            scrollToBottom();
        });
    } catch (error) {
        if (error.name !== 'AbortError') {
            responseBubble.textContent ||= `오류가 발생했습니다: ${error.message}`;
            responseBubble.classList.add('error');
        }
    } finally {
        responseBubble.classList.remove('pending');
        controller = null;
        setStreamingState(false);
        input.focus();
    }
}

async function readEventStream(stream, onData) {
    const reader = stream.getReader();
    const decoder = new TextDecoder();
    let buffer = '';

    while (true) {
        const { value, done } = await reader.read();
        buffer += decoder.decode(value, { stream: !done }).replace(/\r\n/g, '\n');
        const events = buffer.split('\n\n');
        buffer = events.pop() ?? '';

        for (const event of events) {
            const data = event.split('\n')
                .filter((line) => line.startsWith('data:'))
                .map((line) => line.slice(5).replace(/^ /, ''))
                .join('\n');
            if (data && data !== '[DONE]') onData(data);
        }
        if (done) break;
    }
}

function appendMessage(text, role) {
    const row = document.createElement('div');
    row.className = `message-row ${role}`;

    if (role === 'assistant') {
        const avatar = document.createElement('div');
        avatar.className = 'avatar';
        avatar.textContent = 'AI';
        avatar.setAttribute('aria-hidden', 'true');
        row.append(avatar);
    }

    const bubble = document.createElement('div');
    bubble.className = 'bubble';
    bubble.textContent = text;
    row.append(bubble);
    messages.append(row);
    scrollToBottom();
    return bubble;
}

function setStreamingState(streaming) {
    actionButton.classList.toggle('stop', streaming);
    actionButton.querySelector('.send-icon').textContent = streaming ? '■' : '➤';
    actionButton.querySelector('.button-label').textContent = streaming ? '중지' : '전송';
    actionButton.setAttribute('aria-label', streaming ? '응답 중지' : '메시지 전송');
}

function resizeInput() {
    input.style.height = 'auto';
    input.style.height = `${Math.min(input.scrollHeight, 150)}px`;
}

function scrollToBottom() {
    messages.scrollTop = messages.scrollHeight;
}
