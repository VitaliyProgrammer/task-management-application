// Where the JWT lives between page reloads.
//
// Trade-off to know for interviews: localStorage is simple but readable by any
// script on the page (XSS risk). The stricter option is an httpOnly cookie,
// which needs backend changes. For a portfolio demo this is an accepted compromise.

const TOKEN_KEY = 'alijenta.token';

export const tokenStorage = {
  get() {
    return localStorage.getItem(TOKEN_KEY);
  },

  set(token) {
    localStorage.setItem(TOKEN_KEY, token);
  },

  clear() {
    localStorage.removeItem(TOKEN_KEY);
  },
};
