import {
  setActiveNav,
  requireAuthAndLoadProfile,
  wireLogout,
  fillTopUser,
  setYear
} from "./common.js";

function showMsg(type, text){
  const msg = document.getElementById("msg");
  if (!msg) return;
  msg.className = "msg " + (type === "ok" ? "ok" : "err");
  msg.textContent = text;
}

let ITEMS = [
  { name:"Black Wallet", cat:"Other", loc:"Library", desc:"Small wallet, ID inside", status:"FOUND" },
  { name:"Student ID Card", cat:"Card", loc:"Canteen", desc:"Name: Sandeepa", status:"FOUND" },
  { name:"Blue Water Bottle", cat:"Other", loc:"Main Hall", desc:"500ml, sticker on it", status:"FOUND" },
];

function render(list){
  const box = document.getElementById("itemsList");
  if (!box) return;

  if (!list.length){
    box.innerHTML = `<div class="item"><div class="item-title">No items found</div></div>`;
    return;
  }

  box.innerHTML = list.map(i => `
    <div class="item">
      <div class="item-top">
        <div class="item-title">${i.name}</div>
        <div class="badge">${i.status}</div>
      </div>
      <div class="item-meta">
        <div><b>Category:</b> ${i.cat}</div>
        <div><b>Location:</b> ${i.loc}</div>
        <div><b>Note:</b> ${i.desc}</div>
      </div>
    </div>
  `).join("");
}

function hookSearch(){
  const search = document.getElementById("search");
  if (!search) return;

  search.addEventListener("input", () => {
    const q = search.value.trim().toLowerCase();
    const filtered = ITEMS.filter(i =>
      (i.name + " " + i.cat + " " + i.loc + " " + i.desc)
        .toLowerCase()
        .includes(q)
    );
    render(filtered);
  });
}

function hookForm(){
  const form = document.getElementById("lostForm");
  if (!form) return;

  form.addEventListener("submit", (e) => {
    e.preventDefault();

    const name = document.getElementById("itemName")?.value.trim();
    const cat = document.getElementById("category")?.value;
    const loc = document.getElementById("location")?.value.trim();
    const desc = document.getElementById("description")?.value.trim();

    if (!name || !cat || !loc || !desc){
      showMsg("err", "Please fill all fields.");
      return;
    }

    // ✅ demo save (later POST to backend)
    ITEMS.unshift({ name, cat, loc, desc, status:"REPORTED" });
    showMsg("ok", "Lost item reported ✅ (demo). Later send to backend.");
    form.reset();
    render(ITEMS);
  });
}

document.addEventListener("DOMContentLoaded", async () => {
  setYear();
  setActiveNav();
  wireLogout();

  const profile = await requireAuthAndLoadProfile();
  if (!profile) return; // redirect handled in common.js

  fillTopUser(profile);

  render(ITEMS);
  hookSearch();
  hookForm();

  document.getElementById("refreshBtn")?.addEventListener("click", () => render(ITEMS));
});
