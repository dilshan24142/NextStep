// js/auth.js
export function setTokens(accessToken, refreshToken) {
  localStorage.setItem("accessToken", accessToken || "");
  localStorage.setItem("refreshToken", refreshToken || "");
}

export function getAccessToken() {
  return localStorage.getItem("accessToken") || "";
}

export function isLoggedIn() {
  return !!getAccessToken();
}

export function logout() {
  localStorage.removeItem("accessToken");
  localStorage.removeItem("refreshToken");
  localStorage.removeItem("email");
  localStorage.removeItem("role");
  localStorage.removeItem("fullName");
  window.location.href = "login.html";
}

export function getStoredUser() {
  return {
    email: localStorage.getItem("email") || "",
    role: localStorage.getItem("role") || "",
    fullName: localStorage.getItem("fullName") || "",
  };
}
