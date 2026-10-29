(() => {
  const form = document.querySelector('#chat-form');
  const input = document.querySelector('#message-input');
  const sendButton = document.querySelector('#send-button');
  const messages = document.querySelector('#messages');
  const welcome = document.querySelector('#welcome');
  const conversation = document.querySelector('#conversation');
  const bottomAnchor = document.querySelector('#bottom-anchor');
  const newChatButton = document.querySelector('#new-chat');
  const clearChatButton = document.querySelector('#clear-chat');
  let pending = false;

  function scrollToLatest() {
    bottomAnchor.scrollIntoView({ behavior: 'smooth', block: 'end' });
  }

  function addMessage(role, text, extraClass = '') {
    const row = document.createElement('div');
    row.className = `message-row ${role} ${extraClass}`.trim();
    if (role === 'assistant') {
      const avatar = document.createElement('span');
      avatar.className = 'assistant-avatar';
      avatar.setAttribute('aria-hidden', 'true');
      avatar.textContent = 'm';
      row.append(avatar);
    }
    const bubble = document.createElement('div');
    bubble.className = 'bubble';
    bubble.textContent = text;
    row.append(bubble);
    messages.append(row);
    scrollToLatest();
    return row;
  }

  function setPending(value) {
    pending = value;
    input.disabled = value;
    sendButton.disabled = value || !input.value.trim();
    form.setAttribute('aria-busy', String(value));
  }

  function resizeInput() {
    input.style.height = 'auto';
    input.style.height = `${Math.min(input.scrollHeight, 180)}px`;
    sendButton.disabled = pending || !input.value.trim();
  }

  async function sendMessage(text) {
    const message = text.trim();
    if (!message || pending) return;

    welcome.hidden = true;
    addMessage('user', message);
    input.value = '';
    resizeInput();
    setPending(true);

    const typing = document.createElement('div');
    typing.className = 'message-row assistant typing';
    typing.setAttribute('aria-label', '답변 작성 중');
    const avatar = document.createElement('span');
    avatar.className = 'assistant-avatar';
    avatar.setAttribute('aria-hidden', 'true');
    avatar.textContent = 'm';
    const bubble = document.createElement('div');
    bubble.className = 'bubble';
    bubble.setAttribute('role', 'status');
    bubble.setAttribute('aria-label', '답변 작성 중');
    for (let i = 0; i < 3; i += 1) {
      const dot = document.createElement('span');
      dot.className = 'typing-dot';
      bubble.append(dot);
    }
    typing.append(avatar, bubble);
    messages.append(typing);
    scrollToLatest();

    try {
      const response = await fetch('/chats', {
        method: 'POST',
        headers: { 'Content-Type': 'text/plain; charset=UTF-8', 'Accept': 'text/plain' },
        body: message
      });
      const answer = await response.text();
      typing.remove();
      if (!response.ok) {
        const detail = answer.trim();
        throw new Error(detail || `서버에서 오류가 발생했어요. (${response.status})`);
      }
      addMessage('assistant', answer.trim() || '답변이 비어 있어요. 다른 질문을 보내 주세요.');
    } catch (error) {
      typing.remove();
      const messageText = error instanceof TypeError
        ? '서버에 연결하지 못했어요. 잠시 후 다시 시도해 주세요.'
        : error.message;
      addMessage('assistant', messageText, 'error');
    } finally {
      setPending(false);
      input.focus();
      resizeInput();
    }
  }

  form.addEventListener('submit', (event) => {
    event.preventDefault();
    sendMessage(input.value);
  });

  input.addEventListener('input', resizeInput);
  input.addEventListener('keydown', (event) => {
    if (event.key === 'Enter' && !event.shiftKey && !event.isComposing) {
      event.preventDefault();
      form.requestSubmit();
    }
  });

  document.querySelectorAll('.suggestion').forEach((button) => {
    button.addEventListener('click', () => sendMessage(button.dataset.prompt));
  });

  function resetChat() {
    if (pending) return;
    messages.replaceChildren();
    welcome.hidden = false;
    input.value = '';
    resizeInput();
    conversation.scrollTo({ top: 0, behavior: 'smooth' });
    input.focus();
  }

  newChatButton.addEventListener('click', resetChat);
  clearChatButton.addEventListener('click', resetChat);
})();
