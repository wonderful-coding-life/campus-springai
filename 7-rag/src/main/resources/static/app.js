const chatMessages = document.querySelector("#chatMessages");
const chatForm = document.querySelector("#chatForm");
const messageInput = document.querySelector("#messageInput");
const sendButton = document.querySelector("#sendButton");

const welcomeMessage = "안녕하세요. 무엇을 도와드릴까요?";

function renderEmptyState() {
  chatMessages.innerHTML = `<div class="empty-state">${welcomeMessage}</div>`;
}

function removeEmptyState() {
  const emptyState = chatMessages.querySelector(".empty-state");
  if (emptyState) {
    emptyState.remove();
  }
}

function scrollToLatest() {
  chatMessages.scrollTop = chatMessages.scrollHeight;
}

function createMessage(role, text) {
  removeEmptyState();

  const row = document.createElement("article");
  row.className = `message-row ${role}`;

  const avatar = document.createElement("div");
  avatar.className = "avatar";
  avatar.textContent = role === "user" ? "나" : "AI";

  const bubble = document.createElement("div");
  bubble.className = "bubble";
  bubble.textContent = text;

  row.append(avatar, bubble);
  chatMessages.append(row);
  scrollToLatest();

  return row;
}

function createTypingMessage() {
  removeEmptyState();

  const row = document.createElement("article");
  row.className = "message-row assistant";
  row.innerHTML = `
    <div class="avatar">AI</div>
    <div class="bubble" aria-label="응답 작성 중">
      <span class="typing" aria-hidden="true">
        <span></span><span></span><span></span>
      </span>
    </div>
  `;

  chatMessages.append(row);
  scrollToLatest();

  return row;
}

function setLoading(isLoading) {
  sendButton.disabled = isLoading;
  messageInput.disabled = isLoading;
}

function resizeTextarea() {
  messageInput.style.height = "auto";
  messageInput.style.height = `${messageInput.scrollHeight}px`;
}

async function requestChat(message) {
  const response = await fetch("/chats", {
    method: "POST",
    headers: {
      "Content-Type": "text/plain;charset=UTF-8",
    },
    body: message,
  });

  const text = await response.text();

  if (!response.ok) {
    throw new Error(text || "채팅 요청에 실패했습니다.");
  }

  return text;
}

chatForm.addEventListener("submit", async (event) => {
  event.preventDefault();

  const message = messageInput.value.trim();
  if (!message) {
    return;
  }

  createMessage("user", message);
  messageInput.value = "";
  resizeTextarea();
  setLoading(true);

  const typingMessage = createTypingMessage();

  try {
    const reply = await requestChat(message);
    typingMessage.remove();
    createMessage("assistant", reply || "응답 내용이 없습니다.");
  } catch (error) {
    typingMessage.remove();
    createMessage("assistant", `오류가 발생했습니다. ${error.message}`);
  } finally {
    setLoading(false);
    messageInput.focus();
  }
});

messageInput.addEventListener("input", resizeTextarea);

messageInput.addEventListener("keydown", (event) => {
  if (event.key === "Enter" && !event.shiftKey) {
    event.preventDefault();
    chatForm.requestSubmit();
  }
});

renderEmptyState();
resizeTextarea();
