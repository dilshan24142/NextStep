export function qs(id) {
  return document.getElementById(id);
}

export function showMsg(msgEl, type, text) {
  msgEl.className = "msg " + type; // ok | err
  msgEl.textContent = text;
}

export function getQueryParam(name) {
  const p = new URLSearchParams(window.location.search);
  return p.get(name);
}
