import { API_BASE, ENDPOINTS } from "./config.js";
import { getAccessToken, logout, clearSession } from "./token.js";

function setActiveNav(){
  const current = (location.pathname.split("/").pop() || "index.html").toLowerCase();
  document.querySelectorAll(".nav-links a").forEach(a=>{
    const href = (a.getAttribute("href")||"").toLowerCase();
    a.classList.toggle("active", href === current);
  });
}

function setUserUI(profile){
  const userMini = document.getElementById("userMini");
  const logoutBtn = document.getElementById("logoutBtn");

  if(userMini) userMini.style.display="flex";
  if(logoutBtn) logoutBtn.style.display="inline-flex";

  const fullName = `${profile.firstName||""} ${profile.lastName||""}`.trim() || "User";
  document.getElementById("userNameTop").textContent = fullName;
  document.getElementById("userRoleTop").textContent = profile.role || "USER";

  if(profile.profilePicture){
    document.getElementById("avatar").src = profile.profilePicture;
  }

  // optional page fields
  const welcome = document.getElementById("welcomeTitle");
  if(welcome) welcome.textContent = `Welcome, ${profile.firstName || "User"}!`;

  const pName = document.getElementById("pName");
  const pEmail = document.getElementById("pEmail");
  const pRole = document.getElementById("pRole");
  if(pName) pName.textContent = fullName;
  if(pEmail) pEmail.textContent = profile.email || "-";
  if(pRole) pRole.textContent = profile.role || "-";

  localStorage.setItem("fullName", fullName);
  localStorage.setItem("email", profile.email || "");
  localStorage.setItem("role", profile.role || "");
}

async function loadProfile(){
  const token = getAccessToken();
  if(!token){
    window.location.replace("login.html");
    return;
  }

  const res = await fetch(`${API_BASE}${ENDPOINTS.profile}`, {
    headers:{ Authorization:`Bearer ${token}` }
  }).catch(()=>null);

  if(!res){
    // backend down: still show logout
    document.getElementById("logoutBtn").style.display="inline-flex";
    document.getElementById("userMini").style.display="flex";
    document.getElementById("userNameTop").textContent = localStorage.getItem("fullName") || "User";
    document.getElementById("userRoleTop").textContent = localStorage.getItem("role") || "USER";
    return;
  }

  if(res.status===401 || res.status===403){
    clearSession();
    window.location.replace("login.html");
    return;
  }

  const data = await res.json().catch(()=>({}));
  setUserUI(data);
}

document.addEventListener("DOMContentLoaded", async ()=>{
  const year = document.getElementById("year");
  if(year) year.textContent = new Date().getFullYear();

  const logoutBtn = document.getElementById("logoutBtn");
  if(logoutBtn) logoutBtn.addEventListener("click", logout);

  setActiveNav();
  await loadProfile();
});
