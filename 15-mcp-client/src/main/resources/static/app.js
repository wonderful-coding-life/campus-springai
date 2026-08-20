const chat = document.querySelector('#chat');
const welcome = document.querySelector('#welcome');
const messages = document.querySelector('#messages');
const form = document.querySelector('#chat-form');
const input = document.querySelector('#message-input');
const sendButton = document.querySelector('#send-button');

let controller = null;

function resizeInput() {
    input.style.height = 'auto';
    input.style.height = `${Math.min(input.scrollHeight, 150)}px`;
}

function scrollToLatest() {
    requestAnimationFrame(() => chat.scrollTo({ top: chat.scrollHeight, behavior: 'smooth' }));
}

function createMessage(role, text = '') {
    const row = document.createElement('div');
    row.className = `message ${role}`;

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
    scrollToLatest();
    return bubble;
}

function setStreaming(streaming) {
    sendButton.classList.toggle('is-streaming', streaming);
    sendButton.setAttribute('aria-label', streaming ? '응답 중지' : '메시지 전송');
    input.disabled = streaming;
    if (!streaming) {
        controller = null;
        sendButton.disabled = !input.value.trim();
        input.focus();
    }
}

function decodeEventData(rawData) {
    if (rawData === '[DONE]') return '';
    try {
        const value = JSON.parse(rawData);
        return typeof value === 'string' ? value : String(value ?? '');
    } catch {
        return rawData;
    }
}

function consumeEvents(buffer, onData, flush = false) {
    const normalized = buffer.replace(/\r\n/g, '\n');
    const frames = normalized.split('\n\n');
    const remainder = flush ? '' : frames.pop();
    for (const frame of frames) {
        const data = frame.split('\n')
            .filter(line => line.startsWith('data:'))
            .map(line => line.slice(5).replace(/^ /, ''))
            .join('\n');
        if (data) onData(decodeEventData(data));
    }
    if (flush && normalized.trim()) {
        const data = normalized.split('\n')
            .filter(line => line.startsWith('data:'))
            .map(line => line.slice(5).replace(/^ /, ''))
            .join('\n');
        if (data) onData(decodeEventData(data));
    }
    return remainder;
}

async function sendMessage(message) {
    welcome.hidden = true;
    createMessage('user', message);
    const answer = createMessage('assistant');
    answer.classList.add('streaming');
    controller = new AbortController();
    setStreaming(true);

    try {
        const response = await fetch('/chats', {
            method: 'POST',
            headers: { 'Content-Type': 'text/plain;charset=UTF-8', 'Accept': 'text/event-stream' },
            body: message,
            signal: controller.signal
        });
        if (!response.ok) throw new Error(`요청에 실패했습니다. (${response.status})`);
        if (!response.body) throw new Error('스트리밍 응답을 받을 수 없습니다.');

        const reader = response.body.getReader();
        const decoder = new TextDecoder();
        let buffer = '';
        const append = chunk => {
            answer.textContent += chunk;
            scrollToLatest();
        };

        while (true) {
            const { value, done } = await reader.read();
            if (done) break;
            buffer += decoder.decode(value, { stream: true });
            buffer = consumeEvents(buffer, append);
        }
        buffer += decoder.decode();
        consumeEvents(buffer, append, true);
        if (!answer.textContent) answer.textContent = '응답이 없습니다. 다시 시도해 주세요.';
    } catch (error) {
        if (error.name === 'AbortError') {
            if (!answer.textContent) answer.remove();
        } else {
            answer.textContent = error.message || '일시적인 오류가 발생했습니다. 다시 시도해 주세요.';
            answer.classList.add('message-error');
        }
    } finally {
        answer.classList.remove('streaming');
        setStreaming(false);
        scrollToLatest();
    }
}

form.addEventListener('submit', event => {
    event.preventDefault();
    if (controller) {
        controller.abort();
        return;
    }
    const message = input.value.trim();
    if (!message) return;
    input.value = '';
    resizeInput();
    sendMessage(message);
});

input.addEventListener('keydown', event => {
    if (event.key === 'Enter' && !event.shiftKey && !event.isComposing) {
        event.preventDefault();
        form.requestSubmit();
    }
});

input.addEventListener('input', () => {
    resizeInput();
    sendButton.disabled = !input.value.trim();
});

document.querySelectorAll('[data-prompt]').forEach(button => {
    button.addEventListener('click', () => {
        input.value = button.dataset.prompt;
        resizeInput();
        form.requestSubmit();
    });
});

sendButton.disabled = true;
input.focus();
