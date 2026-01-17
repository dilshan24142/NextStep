// js/study-room.js
import {
  setActiveNav,
  requireAuthAndLoadProfile,
  wireLogout,
  fillTopUser,
  setYear,
  getToken,
  clearSession
} from "./common.js";

const API_BASE = "http://localhost:8099";
const BOOK_URL = `${API_BASE}/api/v1/study-room/book`;
const MY_BOOKINGS_URL = `${API_BASE}/api/v1/study-room/my-bookings`;

document.addEventListener("DOMContentLoaded", async () => {
  setYear();
  setActiveNav();
  wireLogout();

  // 🔐 protect page + load profile
  const profile = await requireAuthAndLoadProfile();
  if (!profile) return; // redirect already handled in common.js
  fillTopUser(profile);

  // form submit
  const form = document.getElementById("bookingForm");
  if (form) form.addEventListener("submit", handleSubmit);

  // load bookings on page load
  await loadBookings();
});

// ✅ Submit booking
async function handleSubmit(e) {
  e.preventDefault();

  const token = getToken();
  const msgEl = document.getElementById("msg");

  const roomEl = document.getElementById("room");
  const dateEl = document.getElementById("date");
  const timeEl = document.getElementById("time");

  const payload = {
    room: (roomEl?.value || "").trim(),
    date: (dateEl?.value || "").trim(),
    time: (timeEl?.value || "").trim(),
  };

  if (!payload.room || !payload.date || !payload.time) {
    if (msgEl) {
      msgEl.textContent = "Please fill all fields.";
      msgEl.className = "msg err";
    }
    return;
  }

  try {
    const res = await fetch(BOOK_URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`
      },
      body: JSON.stringify(payload)
    });

    // auth expired
    if (res.status === 401 || res.status === 403) {
      clearSession();
      window.location.replace("login.html");
      return;
    }

    const data = await res.json().catch(() => ({}));

    if (!res.ok) {
      const text = data?.message || data?.errorMessage || "Booking failed";
      if (msgEl) {
        msgEl.textContent = text;
        msgEl.className = "msg err";
      }
      return;
    }

    if (msgEl) {
      msgEl.textContent = data?.message || "Room booked successfully ✅";
      msgEl.className = "msg ok";
    }

    e.target.reset();
    await loadBookings();

  } catch (err) {
    if (msgEl) {
      msgEl.textContent = "Network error. Is backend running?";
      msgEl.className = "msg err";
    }
    console.error(err);
  }
}

// ✅ Load bookings
async function loadBookings() {
  const token = getToken();
  const list = document.getElementById("bookingList");
  const msgEl = document.getElementById("msg");

  if (!list) return;
  list.innerHTML = "";

  try {
    const res = await fetch(MY_BOOKINGS_URL, {
      headers: { Authorization: `Bearer ${token}` }
    });

    // token expired / unauthorized
    if (res.status === 401 || res.status === 403) {
      clearSession();
      window.location.replace("login.html");
      return;
    }

    const data = await res.json().catch(() => []);

    if (!res.ok || !Array.isArray(data)) {
      list.innerHTML = `<li>No bookings found</li>`;
      return;
    }

    if (data.length === 0) {
      list.innerHTML = `<li>No bookings yet.</li>`;
      return;
    }

    data.forEach(b => {
      const li = document.createElement("li");
      li.textContent = `${b.room || "-"} | ${b.date || "-"} | ${b.time || "-"}`;
      list.appendChild(li);
    });

  } catch (err) {
    if (msgEl) {
      msgEl.textContent = "Backend not reachable (demo mode).";
      msgEl.className = "msg err";
    }
    list.innerHTML = `<li>Cannot load bookings right now.</li>`;
    console.error(err);
  }
}
