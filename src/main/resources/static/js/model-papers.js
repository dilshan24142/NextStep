import {
  setActiveNav,
  requireAuthAndLoadProfile,
  wireLogout,
  fillTopUser,
  setYear
} from "./common.js";

const DEMO_PAPERS = [
  { name: "DBMS - Mid Exam Model Paper", faculty: "COMPUTING", semester: "2", module: "CS105.3", year: "2025", file: "#" },
  { name: "Computer Networks - Past Paper", faculty: "COMPUTING", semester: "2", module: "CN101", year: "2024", file: "#" },
  { name: "Statistics - Model Paper", faculty: "SCIENCE", semester: "1", module: "STAT100", year: "2023", file: "#" },
  { name: "Business Maths - Past Paper", faculty: "BUSINESS", semester: "1", module: "BM110", year: "2024", file: "#" },
  { name: "Engineering Mechanics - Model", faculty: "ENGINEERING", semester: "2", module: "ENG200", year: "2025", file: "#" },
  { name: "OOP - Past Paper", faculty: "COMPUTING", semester: "1", module: "CS101", year: "2023", file: "#" }
];

function renderPapers(list) {
  const grid = document.getElementById("papersGrid");
  if (!grid) return;

  if (!list.length) {
    grid.innerHTML = `
      <div class="paper">
        <b>No results</b>
        <div class="paper-meta"><div>Try changing filters</div></div>
      </div>
    `;
    return;
  }

  grid.innerHTML = list.map(p => `
    <div class="paper">
      <div class="paper-top">
        <div class="paper-name">${p.name}</div>
        <div class="paper-badge">${p.year}</div>
      </div>

      <div class="paper-meta">
        <div><span>Faculty:</span> ${p.faculty}</div>
        <div><span>Semester:</span> ${p.semester}</div>
        <div><span>Module:</span> ${p.module}</div>
      </div>

      <div class="paper-actions">
        <button class="btn-mini" type="button" data-action="preview">Preview</button>
        <a class="btn-mini dark" href="${p.file}" data-action="download">Download</a>
      </div>
    </div>
  `).join("");

  // ✅ safer than inline onclick
  grid.querySelectorAll('[data-action="preview"]').forEach(btn => {
    btn.addEventListener("click", () => alert("Preview later ✅"));
  });

  grid.querySelectorAll('[data-action="download"]').forEach(a => {
    a.addEventListener("click", (e) => {
      e.preventDefault();
      alert("Download later ✅");
    });
  });
}

function applyFilters() {
  const faculty = document.getElementById("faculty")?.value || "";
  const semester = document.getElementById("semester")?.value || "";
  const q = (document.getElementById("q")?.value || "").trim().toLowerCase();

  const filtered = DEMO_PAPERS.filter(p => {
    if (faculty && p.faculty !== faculty) return false;
    if (semester && p.semester !== semester) return false;

    if (q) {
      const hay = `${p.name} ${p.module} ${p.faculty} ${p.year}`.toLowerCase();
      if (!hay.includes(q)) return false;
    }

    return true;
  });

  renderPapers(filtered);
}

document.addEventListener("DOMContentLoaded", async () => {
  setYear();
  setActiveNav();
  wireLogout();

  const profile = await requireAuthAndLoadProfile();
  if (!profile) return; // redirects inside common.js

  fillTopUser(profile);

  renderPapers(DEMO_PAPERS);

  document.getElementById("searchBtn")?.addEventListener("click", applyFilters);

  // optional: filter on change (nice UX)
  document.getElementById("faculty")?.addEventListener("change", applyFilters);
  document.getElementById("semester")?.addEventListener("change", applyFilters);
});
