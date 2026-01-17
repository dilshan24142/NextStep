import { API_BASE, ENDPOINTS } from "./config.js";
import { getAccessToken, logout, clearSession } from "./token.js";

export function setActiveNav() {
  const current = (location.pathname.split("/").pop() || "index.html").toLowerCase();
  document.querySelectorAll(".nav-links a").forEach(a => {
    const href = (a.getAttribute("href") || "").toLowerCase();
    a.classList.toggle("active", href === current);
  });
}

export function setYear() {
  const year = document.getElementById("year");
  if (year) year.textContent = new Date().getFullYear();
}

export function wireLogout() {
  const logoutBtn = document.getElementById("logoutBtn");
  if (logoutBtn) logoutBtn.addEventListener("click", logout);
}

export function fillTopUser(profile) {
  const userMini = document.getElementById("userMini");
  const logoutBtn = document.getElementById("logoutBtn");

  if (userMini) userMini.style.display = "flex";
  if (logoutBtn) logoutBtn.style.display = "inline-flex";

  // offline safe
  const fullName =
    `${profile?.firstName || ""} ${profile?.lastName || ""}`.trim() ||
    (localStorage.getItem("fullName") || "User");

  const role =
    (profile?.role || localStorage.getItem("role") || "USER");

  const nameTop = document.getElementById("userNameTop");
  const roleTop = document.getElementById("userRoleTop");
  const avatar = document.getElementById("avatar");

  if (nameTop) nameTop.textContent = fullName;
  if (roleTop) roleTop.textContent = role;
  if (avatar && profile?.profilePicture) avatar.src = profile.profilePicture;

  // store
  localStorage.setItem("fullName", fullName);
  localStorage.setItem("email", profile?.email || localStorage.getItem("email") || "");
  localStorage.setItem("role", role);
}

export async function requireAuthAndLoadProfile() {
  const token = getAccessToken();
  if (!token) {
    window.location.replace("login.html");
    return null;
  }

  const res = await fetch(`${API_BASE}${ENDPOINTS.profile}`, {
    headers: { Authorization: `Bearer ${token}` }
  }).catch(() => null);

  // backend down -> offline mode
  if (!res) {
    return {
      offline: true,
      firstName: (localStorage.getItem("fullName") || "User").split(" ")[0] || "User",
      lastName: "",
      role: localStorage.getItem("role") || "USER"
    };
  }

  if (res.status === 401 || res.status === 403) {
    clearSession();
    window.location.replace("login.html");
    return null;
  }

  const data = await res.json().catch(() => null);
  return data;
}
