import {
  setActiveNav,
  requireAuthAndLoadProfile,
  wireLogout,
  fillTopUser,
  setYear
} from "./common.js";

const DEMO_TIMETABLE = [
  { from: "Maharagama", to: "NSBM", day: "Weekday", time: "07:15 AM", bus: "NS-01" },
  { from: "Nugegoda", to: "NSBM", day: "Weekday", time: "07:45 AM", bus: "NS-02" },
  { from: "Kottawa", to: "NSBM", day: "Weekday", time: "08:10 AM", bus: "NS-03" },
  { from: "NSBM", to: "Maharagama", day: "Weekday", time: "04:30 PM", bus: "NS-01" },
  { from: "NSBM", to: "Nugegoda", day: "Weekday", time: "05:00 PM", bus: "NS-02" },

  { from: "Maharagama", to: "NSBM", day: "Weekend", time: "08:30 AM", bus: "NS-05" },
  { from: "NSBM", to: "Maharagama", day: "Weekend", time: "02:30 PM", bus: "NS-05" },
];

function renderTable(list) {
  const el = document.getElementById("timeTable");
  if (!el) return;

  if (!list.length) {
    el.innerHTML = `<div class="rowt"><div>No routes found</div><div><span>Try another</span></div></div>`;
    return;
  }

  el.innerHTML = list.map(r => `
    <div class="rowt">
      <div>${r.from} → ${r.to}</div>
      <div>${r.time} <span>(${r.bus})</span></div>
    </div>
  `).join("");
}

function searchRoutes() {
  const from = document.getElementById("fromStop")?.value || "";
  const to = document.getElementById("toStop")?.value || "";
  const day = document.getElementById("day")?.value || "Weekday";

  const pillDay = document.getElementById("pillDay");
  if (pillDay) pillDay.textContent = day;

  const results = DEMO_TIMETABLE.filter(r =>
    r.day === day && r.from === from && r.to === to
  );

  renderTable(results);

  // update demo status
  if (results[0]) {
    const busNo = document.getElementById("busNo");
    const currentStop = document.getElementById("currentStop");
    const eta = document.getElementById("eta");

    if (busNo) busNo.textCo
