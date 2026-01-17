import { API_BASE } from "./config.js";
import { qs, showMsg, getQueryParam } from "./utils.js";

const form = qs("verifyForm");
const msg = qs("msg");
const emailEl = qs("email");
const otpEl = qs("otp");
const resendBtn = qs("resendBtn");
const timerEl = qs("timer");

const emailFromQuery = getQueryParam("email");
if (emailFromQuery) emailEl.value = emailFromQuery;

let cooldown = 30;

function startCooldown() {
  resendBtn.disabled = true;
  let t = cooldown;

  const tick = () => {
    const mm = String(Math.floor(t / 60)).padStart(2, "0");
    const ss = String(t % 60).padStart(2, "0");
    timerEl.textContent = `${mm}:${ss}`;

    if (t <= 0) {
      resendBtn.disabled = false;
      timerEl.textContent = "00:00";
      return;
    }
    t--;
    setTimeout(tick, 1000);
  };
  tick();
}
startCooldown();

// ✅ OTP VERIFY endpoint
// (ඔයාගේ backend එකේ path එක confirm කරගන්න: /verify , /verify-otp , /register/verify වගේ වෙනස් නම් වෙනස් කරලා දාන්න)
const VERIFY_URL = `${API_BASE}/api/v1/auth/verify`;
const RESEND_URL = `${API_BASE}/api/v1/auth/resend-otp`;

form.addEventListener("submit", async (e) => {
  e.preventDefault();

  const payload = {
    email: emailEl.value.trim(),
    otp: otpEl.value.trim()
  };

  try {
    const res = await fetch(VERIFY_URL, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });

    const body = await res.json().catch(() => ({}));

    if (!res.ok) {
      const text =
        body?.message ||
        body?.errorMessage ||
        (typeof body === "object" ? Object.values(body).join("\n") : "") ||
        "OTP verify failed";
      showMsg(msg, "err", text);
      return;
    }

    showMsg(msg, "ok", "Verified ✅ Now login.");
    setTimeout(() => {
      window.location.href = `login.html?email=${encodeURIComponent(payload.email)}`;
    }, 700);

  } catch {
    showMsg(msg, "err", "Network error. Is backend running on 8099?");
  }
});

resendBtn.addEventListener("click", async () => {
  const email = emailEl.value.trim();
  if (!email) {
    showMsg(msg, "err", "Enter email first.");
    return;
  }

  try {
    resendBtn.disabled = true;

    const res = await fetch(RESEND_URL, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email })
    });

    const body = await res.json().catch(() => ({}));

    if (!res.ok) {
      const text =
        body?.message ||
        body?.errorMessage ||
        (typeof body === "object" ? Object.values(body).join("\n") : "") ||
        "Resend failed";
      showMsg(msg, "err", text);
      resendBtn.disabled = false;
      return;
    }

    showMsg(msg, "ok", body?.message || "OTP resent ✅ Check email.");
    otpEl.value = "";
    startCooldown();

  } catch {
    showMsg(msg, "err", "Network error. Is backend running on 8099?");
    resendBtn.disabled = false;
  }
});
