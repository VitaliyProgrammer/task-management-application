// Date helpers. Months are always written in full: "November 6, 2026",
// "6 листопада 2026 р.", "6 ноября 2026 г." — Intl does the translation for us.

const LOCALES = {
  en: 'en-US',
  uk: 'uk-UA',
  ru: 'ru-RU',
};

const toLocale = (language) => LOCALES[language] ?? LOCALES.en;

// "2026-11-06" from the API is a plain date. new Date("2026-11-06") would treat it
// as UTC midnight and could show the previous day, so we build it from parts.
export function parseDate(value) {
  if (!value) {
    return null;
  }

  const [datePart] = String(value).split('T');
  const [year, month, day] = datePart.split('-').map(Number);

  return new Date(year, month - 1, day);
}

export function formatDate(value, language, { withYear = true } = {}) {
  const date = parseDate(value);

  if (!date) {
    return '—';
  }

  return new Intl.DateTimeFormat(toLocale(language), {
    day: 'numeric',
    month: 'long',
    ...(withYear && { year: 'numeric' }),
  }).format(date);
}

export function formatDateTime(value, language) {
  if (!value) {
    return '—';
  }

  return new Intl.DateTimeFormat(toLocale(language), {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(new Date(value));
}

// Whole days from today until the date (negative = overdue).
export function daysFromToday(value) {
  const date = parseDate(value);

  if (!date) {
    return null;
  }

  const today = new Date();
  today.setHours(0, 0, 0, 0);

  return Math.round((date - today) / 86_400_000);
}

// "2026-11-06" for <input type="date">
export function toInputDate(date) {
  const pad = (number) => String(number).padStart(2, '0');

  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;
}
