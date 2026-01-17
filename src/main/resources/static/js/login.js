import { API_BASE } from "./config.js";
import { qs, showMsg, getQueryParam } from "./utils.js";

const form = qs("loginForm");
const msg = qs("msg");
const emailEl = qs("email");
const passEl = qs("password");

const emailFromQuery = getQueryParam("email");
if (emailFromQuery) emailEl.value = emailFromQuery;

form.addEventListener("submit", async (e) => {
  e.preventDefault();

  const payload = {
    email: emailEl.value.trim(),
    password: passEl.value.trim()
  };

  try {
    const res = await fetch(`${API_BASE}/api/v1/auth/login`, {
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
        "Login failed";

      // ✅ If not verified -> go verify page
      if (String(text).toLowerCase().includes("not verified")) {
        showMsg(msg, "err", text + "\nPlease verify OTP first.");
        setTimeout(() => {
          window.location.href = `verify-otp.html?email=${encodeURIComponent(payload.email)}`;
        }, 800);
        return;
      }

      showMsg(msg, "err", text);
      return;
    }

    // ✅ Save tokens if your login response returns them
    if (body.accessToken) localStorage.setItem("accessToken", body.accessToken);
    if (body.refreshToken) localStorage.setItem("refreshToken", body.refreshToken);

    showMsg(msg, "ok", "Login success ✅");
    // TODO: redirect dashboard
    setTimeout(() => (window.location.href = "index.html"), 700);

  } catch {
    showMsg(msg, "err", "Network error. Is backend running on 8099?");
  }
});
