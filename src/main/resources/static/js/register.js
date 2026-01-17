import { API_BASE } from "./config.js";
import { qs, showMsg } from "./utils.js";

const form = qs("registerForm");
const msg = qs("msg");

form.addEventListener("submit", async (e) => {
  e.preventDefault();

  // ✅ build payload exactly like RegisterRequest DTO
  const payload = {
    firstName: qs("firstName").value.trim(),
    lastName: qs("lastName").value.trim(),
    email: qs("email").value.trim(),
    password: qs("password").value.trim(),
    gender: qs("gender").value,
    phoneNumber: qs("phoneNumber").value.trim(),
    role: qs("role").value
  };

  try {
    const res = await fetch(`${API_BASE}/api/v1/auth/register`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });

    const body = await res.json().catch(() => ({}));

    if (!res.ok) {
      // Spring validation errors come as {field: message}
      const text =
        body?.message ||
        body?.errorMessage ||
        (typeof body === "object" ? Object.values(body).join("\n") : "") ||
        "Registration failed";
      showMsg(msg, "err", text);
      return;
    }

    showMsg(msg, "ok", body?.message || "Registered ✅ OTP sent to email. Please verify.");
    setTimeout(() => {
      window.location.href = `verify-otp.html?email=${encodeURIComponent(payload.email)}`;
    }, 700);

  } catch (err) {
    showMsg(msg, "err", "Network error. Is backend running on 8099?");
  }
});
