import { useTranslation } from 'react-i18next';
import { LANGUAGES } from '../i18n';

export function LanguageSwitcher({ className = '' }) {
  const { i18n, t } = useTranslation();

  return (
    <div className={`segmented ${className}`} role="group" aria-label={t('common.language')}>
      {LANGUAGES.map((language) => {
        const isActive = i18n.language === language.code;

        return (
          <button
            key={language.code}
            type="button"
            className={`segmented__item ${isActive ? 'segmented__item--active' : ''}`}
            aria-pressed={isActive}
            onClick={() => i18n.changeLanguage(language.code)}
          >
            {language.label}
          </button>
        );
      })}
    </div>
  );
}
