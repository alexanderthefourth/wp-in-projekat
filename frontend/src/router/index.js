import { createRouter, createWebHistory } from "vue-router";
import RegisteredUser from "@/views/RegisteredUser.vue";
import RegisteredAdmin from "@/views/RegisteredAdmin.vue";
import LoginView from "@/views/LoginView.vue";
import RegisterView from "@/views/RegisterView.vue";
import BlockedUser from "@/views/BlockedUser.vue";
import { Users } from "@/services/api.js";

const routes = [
  { path: "/", redirect: "/login" },

  {
    path: "/registered",
    name: "registered",
    component: RegisteredUser,
    meta: { requiresAuth: true, allowedRoles: ["USER", "ADMIN"] },
  },
  {
    path: "/registered-admin",
    name: "registered-admin",
    component: RegisteredAdmin,
    meta: { requiresAuth: true, allowedRoles: ["ADMIN"] },
  },
  {
    path: "/login",
    name: "login",
    component: LoginView,
    meta: { requiresGuest: true },
  },
  {
    path: "/register",
    name: "register",
    component: RegisterView,
    meta: { requiresGuest: true },
  },
  {
    path: "/blocked",
    name: "blocked",
    component: BlockedUser,
  },
  { path: "/:pathMatch(.*)*", redirect: "/login" },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

async function verifySession() {
  try {
    const raw = localStorage.getItem("user");
    if (!raw) return null;

    const user = JSON.parse(raw);

    await Users.getAll();

    return user;
  } catch (error) {
    console.warn("invalid session, clearing local storage");
    localStorage.removeItem("user");
    return null;
  }
}

function hasAllowedRole(user, allowedRoles) {
  return allowedRoles.includes(user.role);
}

function getDefaultRoute(user) {
  if (user.blocked) return "/blocked";
  return user.role === "ADMIN" ? "/registered-admin" : "/registered";
}

router.beforeEach(async (to) => {
  const user = await verifySession();
  //console.log("Navigacija:", to.path, "| Korisnik:", user);

  if (to.meta.requiresGuest) {
    if (user) {
      return getDefaultRoute(user);
    }
    return true;
  }

  if (to.meta.requiresAuth) {
    if (!user) {
      return "/login";
    }

    if (user.blocked && to.path !== "/blocked") {
      return "/blocked";
    }

    if (to.path === "/blocked" && !user.blocked) {
      return getDefaultRoute(user);
    }

    if (to.meta.allowedRoles && !hasAllowedRole(user, to.meta.allowedRoles)) {
      console.warn("Pristup zabranjen za role:", user.role);
      return getDefaultRoute(user);
    }

    return true;
  }

  if (to.path === "/blocked") {
    if (!user) return "/login";
    if (!user.blocked) return getDefaultRoute(user);
    return true;
  }

  return true;
});

export default router;
