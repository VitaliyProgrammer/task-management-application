import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import en from './locales/en.json';
import uk from './locales/uk.json';
import ru from './locales/ru.json';

const STORAGE_KEY = 'alijenta.language';

// "code" is what i18next and Intl use, "label" is what the switcher shows.
export const LANGUAGES = [
  { code: 'en', label: 'EN' },
  { code: 'uk', label: 'UA' },
  { code: 'ru', label: 'RU' },
];

function savedLanguage() {
  const saved = localStorage.getItem(STORAGE_KEY);

  return LANGUAGES.some((language) => language.code === saved) ? saved : 'en';
}

i18n.use(initReactI18next).init({
  resources: {
    en: { translation: en },
    uk: { translation: uk },
    ru: { translation: ru },
  },

  lng: savedLanguage(),
  fallbackLng: 'en',

  // React already escapes values, so i18next does not need to.
  interpolation: { escapeValue: false },
});

// Remember the choice and keep <html lang> correct for screen readers.
i18n.on('languageChanged', (language) => {
  localStorage.setItem(STORAGE_KEY, language);
  document.documentElement.lang = language;
});

document.documentElement.lang = i18n.language;

export default i18n;
