import { setActiveNav, requireAuthAndLoadProfile, wireLogout, fillTopUser, setYear } from "./common.js";

document.addEventListener("DOMContentLoaded", async () => {
  setYear();
  setActiveNav();
  wireLogout();

  const profile = await requireAuthAndLoadProfile();
  if (!profile) return;
  fillTopUser(profile);
});
