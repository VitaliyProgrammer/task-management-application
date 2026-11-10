import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../../auth/AuthContext';
import { authApi } from '../../api/endpoints';
import { ErrorAlert } from '../../components/States';
import { Icon } from '../../components/Icon';
import { AuthLayout } from './AuthLayout';

const EMPTY_FORM = {
  firstName: '',
  lastName: '',
  email: '',
  password: '',
  repeatPassword: '',
};

const MIN_PASSWORD_LENGTH = 8;

export function RegisterPage() {
  const { t } = useTranslation();
  const { login } = useAuth();
  const navigate = useNavigate();

  const [form, setForm] = useState(EMPTY_FORM);
  const [error, setError] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const updateField = (event) => {
    setForm({ ...form, [event.target.name]: event.target.value });
  };

  // Checked in the browser for instant feedback; the backend checks again.
  const passwordsMismatch = form.repeatPassword !== '' && form.password !== form.repeatPassword;

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (passwordsMismatch) {
      return;
    }

    setError(null);
    setIsSubmitting(true);

    try {
      await authApi.register(form);
      await login({ email: form.email, password: form.password });
      navigate('/dashboard', { replace: true });
    } catch (requestError) {
      setError(requestError);
    } finally {
      setIsSubmitting(false);
    }
  };

  const perks = (
    <ul style={{ margin: 0, padding: 0, listStyle: 'none', display: 'grid', gap: 12 }}>
      {['perk1', 'perk2', 'perk3'].map((perk) => (
        <li key={perk} style={{ display: 'flex', alignItems: 'center', gap: 10, fontWeight: 700 }}>
          <Icon name="check" strokeWidth={2.5} />
          {t(`register.${perk}`)}
        </li>
      ))}
    </ul>
  );

  return (
    <AuthLayout title={t('register.brandTitle')} text={t('register.brandText')} aside={perks}>
      <div>
        <h2 className="page-title" style={{ fontSize: 30 }}>
          {t('register.title')}
        </h2>
        <p className="field__hint" style={{ fontSize: 15, marginTop: 6 }}>
          {t('register.subtitle')}
        </p>
      </div>

      <form onSubmit={handleSubmit} style={{ display: 'grid', gap: 16 }}>
        <ErrorAlert error={error} />

        <div
          className="form-grid"
          style={{ gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))' }}
        >
          <label className="field">
            {t('fields.firstName')}
            <input
              className="input"
              name="firstName"
              value={form.firstName}
              onChange={updateField}
              required
            />
          </label>

          <label className="field">
            {t('fields.lastName')}
            <input
              className="input"
              name="lastName"
              value={form.lastName}
              onChange={updateField}
              required
            />
          </label>
        </div>

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
            autoComplete="new-password"
            minLength={MIN_PASSWORD_LENGTH}
            value={form.password}
            onChange={updateField}
            required
          />
          <span className="field__hint">
            {t('register.passwordHint', { count: MIN_PASSWORD_LENGTH })}
          </span>
        </label>

        <label className="field">
          {t('fields.repeatPassword')}
          <input
            className={`input ${passwordsMismatch ? 'input--invalid' : ''}`}
            type="password"
            name="repeatPassword"
            autoComplete="new-password"
            value={form.repeatPassword}
            onChange={updateField}
            aria-invalid={passwordsMismatch}
            required
          />
          {passwordsMismatch && (
            <span className="field__error">{t('register.passwordsMismatch')}</span>
          )}
        </label>

        <button type="submit" className="btn btn--primary btn--block" disabled={isSubmitting}>
          {isSubmitting ? t('common.loading') : t('register.submit')}
        </button>
      </form>

      <p className="field__hint" style={{ textAlign: 'center', fontSize: 14 }}>
        {t('register.haveAccount')} <Link to="/login">{t('register.logIn')}</Link>
      </p>
    </AuthLayout>
  );
}
