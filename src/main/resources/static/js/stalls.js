// js/stalls.js
import {
  setActiveNav,
  requireAuthAndLoadProfile,
  wireLogout,
  fillTopUser,
  setYear
} from "./common.js";

// your images in: src/main/resources/static/images/
const STALLS = [
  { title:"Green Bite Cafeteria", icon:"⚡", img:"images/burger.jpg",      l1:"Open | File",   l2:"View Menu" },
  { title:"BBQ Chicken",         icon:"🍗", img:"images/bbq chicken.jpg", l1:"Upload Files",  l2:"View Menu" },
  { title:"Biriyani",            icon:"🍛", img:"images/biriyani.jpg",    l1:"Upload Files",  l2:"View Menu" },

  { title:"Fried Rice",          icon:"🍱", img:"images/fried rice.jpg",  l1:"Open",          l2:"View Menu" },
  { title:"Hotdog",              icon:"🌭", img:"images/hotdog.jpg",      l1:"Open",          l2:"View Menu" },
  { title:"Lasangna",            icon:"🍝", img:"images/lasangna.jpg",    l1:"Open",          l2:"View Menu" },

  { title:"Sandwitch",           icon:"🥪", img:"images/sandwitch.jpg",   l1:"Open",          l2:"View Menu" },
];

function renderCards() {
  const grid = document.getElementById("stallsGrid");
  const count = document.getElementById("stallsCount");
  if (!grid) return;

  if (count) count.textContent = `${STALLS.length} Stalls Active Today`;

  grid.innerHTML = STALLS.map(s => `
    <article class="cardx">
      <div class="cardx-top">
        <div class="cardx-title">${s.title}</div>
        <div class="cardx-icon">${s.icon}</div>
      </div>

      <div class="cardx-body">
        <div class="photo">
          <img src="${encodeURI(s.img)}" alt="${s.title}"
               onerror="this.src='https://picsum.photos/400/260?random=2'">
        </div>

        <div class="side">
          <span class="badge-open">Open</span>

          <div class="lines">
            <div>${s.l1}</div>
            <div>${s.l2}</div>
          </div>

          <div class="actions">
            <button class="btn-mini" type="button">Open</button>
            <button class="btn-mini dark" type="button">View Menu</button>
          </div>
        </div>
      </div>
    </article>
  `).join("");

  grid.querySelectorAll(".btn-mini").forEach(btn => {
    btn.addEventListener("click", () => alert("Feature later ✅"));
  });
}

document.addEventListener("DOMContentLoaded", async () => {
  // UI first
  renderCards();

  setYear();
  setActiveNav();
  wireLogout();

  document.getElementById("reportLostBtn")?.addEventListener("click", () => {
    window.location.href = "lost-found.html";
  });

  const profile = await requireAuthAndLoadProfile();
  if (!profile) return;
  fillTopUser(profile);
});
