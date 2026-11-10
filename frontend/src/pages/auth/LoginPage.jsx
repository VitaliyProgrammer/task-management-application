import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../../auth/AuthContext';
import { ErrorAlert } from '../../components/States';
import { TASK_STATUSES, statusStyle } from '../../lib/taskMeta';
import { AuthLayout } from './AuthLayout';

// The demo account is seeded by the backend (Liquibase "demo" context).
const DEMO_EMAIL = import.meta.env.VITE_DEMO_EMAIL || 'demo@alijenta.dev';
const DEMO_PASSWORD = import.meta.env.VITE_DEMO_PASSWORD || 'Alijenta2026';

export function LoginPage() {
  const { t } = useTranslation();
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const [form, setForm] = useState({ email: '', password: '' });
  const [error, setError] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const updateField = (event) => {
    setForm({ ...form, [event.target.name]: event.target.value });
  };

  const fillDemoAccount = () => {
    setForm({ email: DEMO_EMAIL, password: DEMO_PASSWORD });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError(null);
    setIsSubmitting(true);

    try {
      await login(form);
      navigate(location.state?.from ?? '/dashboard', { replace: true });
    } catch (requestError) {
      setError(requestError);
    } finally {
      setIsSubmitting(false);
    }
  };

  const statusList = (
    <ul style={{ margin: 0, padding: 0, listStyle: 'none', display: 'grid', gap: 12 }}>
      {TASK_STATUSES.map((status) => (
        <li
          key={status}
          style={{ display: 'flex', alignItems: 'center', gap: 12, fontWeight: 700, fontSize: 14 }}
        >
          <span className="dot" style={{ color: statusStyle(status)['--pill-dot'] }} />
          {t(`taskStatus.${status}`)}
        </li>
      ))}
    </ul>
  );

  return (
    <AuthLayout title={t('login.brandTitle')} text={t('login.brandText')} aside={statusList}>
      <div>
        <h2 className="page-title" style={{ fontSize: 30 }}>
          {t('login.title')}
        </h2>
        <p className="field__hint" style={{ fontSize: 15, marginTop: 6 }}>
          {t('login.subtitle')}
        </p>
      </div>

      <div className="alert alert--info">
        <strong>{t('login.demoTitle')}</strong> {DEMO_EMAIL} · {DEMO_PASSWORD}{' '}
        <button type="button" className="btn btn--ghost btn--small" onClick={fillDemoAccount}>
          {t('login.useDemo')}
        </button>
      </div>

      <form onSubmit={handleSubmit} style={{ display: 'grid', gap: 16 }}>
        <ErrorAlert error={error} />

        <label className="field">
          {t('fields.email')}
          <input
            className="input"
            type="email"
            name="email"
            autoComplete="email"
            value={form.email}
            onChange={updateField}
            required
          />
        </label>

        <label className="field">
          {t('fields.password')}
          <input
            className="input"
            type="password"
            name="password"
            autoComplete="current-password"
            value={form.password}
            onChange={updateField}
            required
          />
        </label>

        <button type="submit" className="btn btn--primary btn--block" disabled={isSubmitting}>
          {isSubmitting ? t('common.loading') : t('login.submit')}
        </button>
      </form>

      <p className="field__hint" style={{ textAlign: 'center', fontSize: 14 }}>
        {t('login.noAccount')} <Link to="/register">{t('login.createOne')}</Link>
      </p>
    </AuthLayout>
  );
}
