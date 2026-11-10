import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { Logo } from '../components/Logo';

export function NotFoundPage() {
  const { t } = useTranslation();

  return (
    <main
      className="state"
      style={{ minHeight: '100vh', display: 'grid', placeContent: 'center', gap: 20 }}
    >
      <Logo />

      <h1 className="page-title">404</h1>
      <p>{t('notFound.text')}</p>

      <Link to="/" className="btn btn--primary">
        {t('notFound.home')}
      </Link>
    </main>
  );
}
