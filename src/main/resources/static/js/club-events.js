// js/club-events.js
import {
  setActiveNav,
  requireAuthAndLoadProfile,
  wireLogout,
  fillTopUser,
  setYear
} from "./common.js";

/* ================= DEMO DATA ================= */

let EVENTS = [
  { id:1, title:"Hackathon 2026", faculty:"COMPUTING", date:"2026-02-10", venue:"Auditorium", desc:"Team coding challenge + prizes." },
  { id:2, title:"Business Pitch Night", faculty:"BUSINESS", date:"2026-02-15", venue:"B1 Hall", desc:"Pitch your startup idea." },
  { id:3, title:"Robotics Workshop", faculty:"ENGINEERING", date:"2026-02-20", venue:"Eng Lab", desc:"Hands-on robotics session." },
  { id:4, title:"Science Quiz Bowl", faculty:"SCIENCE", date:"2026-02-25", venue:"S2 Hall", desc:"Inter-batch quiz competition." },
];

let MY_REGS = [];

/* ================= RENDER ================= */

function renderEvents(list){
  const box = document.getElementById("eventsList");
  if (!box) return;

  if (!list.length){
    box.innerHTML = `<div class="event"><div class="event-title">No events found</div></div>`;
    return;
  }

  box.innerHTML = list.map(e => `
    <div class="event">
      <div class="event-top">
        <div class="event-title">${e.title}</div>
        <div class="badge">${e.faculty}</div>
      </div>
      <div class="meta">
        <div><b>Date:</b> ${e.date}</div>
        <div><b>Venue:</b> ${e.venue}</div>
        <div><b>About:</b> ${e.desc}</div>
      </div>
    </div>
  `).join("");
}

function fillSelect(){
  const sel = document.getElementById("eventSelect");
  if (!sel) return;

  sel.innerHTML =
    `<option value="">Select</option>` +
    EVENTS.map(e =>
      `<option value="${e.id}">${e.title} (${e.date})</option>`
    ).join("");
}

function renderMyRegs(){
  const box = document.getElementById("myRegs");
  if (!box) return;

  if (!MY_REGS.length){
    box.innerHTML = `<div class="muted small">No registrations yet.</div>`;
    return;
  }

  box.innerHTML = MY_REGS.map(r => `
    <div class="my-item">
      <div>
        ${r.title}
        <div><small>${r.date} • ${r.faculty}</small></div>
      </div>
      <div class="badge">REGISTERED</div>
    </div>
  `).join("");
}

/* ================= LOGIC ================= */

function hookSearch(){
  const search = document.getElementById("search");
  const filter = document.getElementById("filter");
  if (!search || !filter) return;

  const run = () => {
    const q = search.value.trim().toLowerCase();
    const f = filter.value;

    const filtered = EVENTS.filter(e => {
      const okFaculty = (f === "ALL") || (e.faculty === f);
      const okText = (e.title + " " + e.desc + " " + e.venue)
        .toLowerCase()
        .includes(q);
      return okFaculty && okText;
    });

    renderEvents(filtered);
  };

  search.addEventListener("input", run);
  filter.addEventListener("change", run);
}

function hookRegister(){
  const form = document.getElementById("regForm");
  const sel = document.getElementById("eventSelect");
  const note = document.getElementById("note");
  const msg = document.getElementById("msg");

  if (!form || !sel) return;

  form.addEventListener("submit", (e) => {
    e.preventDefault();

    const id = Number(sel.value);
    if (!id){
      msg.textContent = "Select an event first.";
      msg.className = "msg err";
      return;
    }

    const event = EVENTS.find(x => x.id === id);
    if (!event){
      msg.textContent = "Event not found.";
      msg.className = "msg err";
      return;
    }

    if (MY_REGS.some(x => x.id === id)){
      msg.textContent = "Already registered for this event.";
      msg.className = "msg err";
      return;
    }

    MY_REGS.unshift({ ...event, note: (note?.value || "").trim() });
    msg.textContent = "Registered ✅ (demo).";
    msg.className = "msg ok";

    form.reset();
    fillSelect();
    renderMyRegs();
  });
}

/* ================= INIT ================= */

document.addEventListener("DOMContentLoaded", async () => {
  setYear();
  setActiveNav();
  wireLogout();

  const profile = await requireAuthAndLoadProfile();
  if (!profile) return;   // redirect handled in common.js

  fillTopUser(profile);

  renderEvents(EVENTS);
  fillSelect();
  renderMyRegs();
  hookSearch();
  hookRegister();

  document.getElementById("refreshBtn")
    ?.addEventListener("click", () => {
      renderEvents(EVENTS);
      fillSelect();
    });
});
