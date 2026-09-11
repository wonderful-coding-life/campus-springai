(() => {
  const form = document.querySelector('#chat-form');
  const input = document.querySelector('#message-input');
  const username = document.querySelector('#username');
  const messages = document.querySelector('#messages');
  const sendButton = document.querySelector('#send-button');
  const newChat = document.querySelector('#new-chat');
  let controller = null;
  let conversationId = crypto.randomUUID();

  const scrollToBottom = () => { messages.scrollTop = messages.scrollHeight; };
  const resizeInput = () => { input.style.height = 'auto'; input.style.height = `${Math.min(input.scrollHeight, 130)}px`; };
  const addMessage = (text, role, typing = false) => {
    const message = document.createElement('div');
    message.className = `message ${role}-message`;
    if (role === 'assistant') {
      const avatar = document.createElement('div'); avatar.className = 'avatar'; avatar.textContent = 'AI'; message.append(avatar);
    }
    const bubble = document.createElement('div'); bubble.className = `bubble${typing ? ' typing' : ''}`; bubble.textContent = text; message.append(bubble);
    messages.append(message); scrollToBottom(); return bubble;
  };
  const setStreaming = (value) => { form.classList.toggle('is-streaming', value); input.disabled = value; sendButton.setAttribute('aria-label', value ? '응답 생성 중지' : '메시지 전송'); };
  const stopStreaming = () => { controller?.abort(); };

  // SSE data lines are JSON strings because the API serializes each content chunk.
  const consumeStream = async (response, bubble) => {
    const reader = response.body.getReader(); const decoder = new TextDecoder(); let buffer = '';
    const appendEvent = (event) => {
      const data = event.split(/\r?\n/).filter(line => line.startsWith('data:')).map(line => line.slice(5).trimStart()).join('\n');
      if (!data || data === '[DONE]') return;
      try { bubble.textContent += JSON.parse(data); } catch { bubble.textContent += data; }
      scrollToBottom();
    };
    while (true) {
      const { value, done } = await reader.read();
      buffer += decoder.decode(value || new Uint8Array(), { stream: !done });
      const events = buffer.split(/\r?\n\r?\n/); buffer = events.pop(); events.forEach(appendEvent);
      if (done) { if (buffer.trim()) appendEvent(buffer); break; }
    }
  };

  const send = async () => {
    const text = input.value.trim(); if (!text || controller) return;
    const name = username.value.trim() || '고객';
    addMessage(text, 'user'); input.value = ''; resizeInput();
    const bubble = addMessage('', 'assistant', true);
    controller = new AbortController(); setStreaming(true);
    try {
      const params = new URLSearchParams({ username: name, conversationId });
      const response = await fetch(`/chats?${params}`, { method: 'POST', headers: { 'Content-Type': 'text/plain; charset=UTF-8', Accept: 'text/event-stream' }, body: text, signal: controller.signal });
      if (!response.ok) throw new Error(`요청 실패 (${response.status})`);
      if (!response.body) throw new Error('응답 스트림을 읽을 수 없습니다.');
      await consumeStream(response, bubble);
      if (!bubble.textContent) bubble.textContent = '응답 내용이 없습니다.';
    } catch (error) {
      if (error.name === 'AbortError') bubble.textContent ||= '응답 생성을 중지했습니다.';
      else bubble.textContent = `문제가 발생했습니다. 잠시 후 다시 시도해 주세요. (${error.message})`;
    } finally { bubble.classList.remove('typing'); controller = null; setStreaming(false); input.focus(); scrollToBottom(); }
  };

  form.addEventListener('submit', event => { event.preventDefault(); controller ? stopStreaming() : send(); });
  input.addEventListener('input', resizeInput);
  input.addEventListener('keydown', event => { if (event.key === 'Enter' && !event.shiftKey) { event.preventDefault(); controller ? stopStreaming() : send(); } });
  newChat.addEventListener('click', () => { if (controller) stopStreaming(); conversationId = crypto.randomUUID(); messages.replaceChildren(); addMessage('새 대화를 시작했습니다. 무엇을 도와드릴까요?', 'assistant'); input.focus(); });
  resizeInput(); input.focus();
})();
