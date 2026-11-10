// Helpers for showing people: full name, initials, avatar colors.

const AVATAR_COLORS = [
  { bg: '#DCF2E4', fg: '#1A6B41' },
  { bg: '#FCE7F3', fg: '#9D174D' },
  { bg: '#E0F2FE', fg: '#075985' },
  { bg: '#FEF3C7', fg: '#92400E' },
  { bg: '#EDE9FE', fg: '#5B21B6' },
];

export function fullName(user) {
  if (!user) {
    return '—';
  }

  return `${user.firstName} ${user.lastName}`.trim();
}

export function initials(user) {
  if (!user) {
    return '?';
  }

  return `${user.firstName?.[0] ?? ''}${user.lastName?.[0] ?? ''}`.toUpperCase();
}

// The same person always gets the same color.
export function avatarColors(user) {
  const index = (user?.id ?? 0) % AVATAR_COLORS.length;

  return AVATAR_COLORS[index];
}

export function isAdmin(user) {
  return Boolean(user?.roles?.includes('ADMIN'));
}
