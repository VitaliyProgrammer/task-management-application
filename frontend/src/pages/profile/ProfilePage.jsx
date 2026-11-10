import { useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../../auth/AuthContext';
import { queryKeys } from '../../hooks/queries';
import { profileApi } from '../../api/endpoints';
import { Avatar } from '../../components/Badges';
import { ErrorAlert } from '../../components/States';
import { DropboxIcon, Icon } from '../../components/Icon';
import { fullName } from '../../lib/people';
import styles from './ProfilePage.module.css';

export function ProfilePage() {
  const { t } = useTranslation();
  const { user, isAdmin, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login', { replace: true });
  };

  return (
    <main className="page" style={{ maxWidth: 1040 }}>
      <header className={styles.header}>
        <Avatar user={user} size={72} />

        <div>
          <h1 className="page-title">{fullName(user)}</h1>
          <p className="page-subtitle">
            {user.email} {isAdmin && <span className={styles.adminBadge}>ADMIN</span>}
          </p>
        </div>
      </header>

      <PersonalInfo user={user} />

      <section className={styles.integrations}>
        <h2 className={styles.sectionTitle}>{t('profile.integrations')}</h2>

        <div className={styles.grid}>
          <TelegramCard user={user} />
          <EmailCard user={user} />
          <CalendarCard user={user} />
          <DropboxCard />
        </div>
      </section>

      <section className={`card ${styles.logout}`}>
        <div>
          <h2 className="card__title">{t('profile.logoutTitle')}</h2>
          <p className="field__hint" style={{ fontSize: 14, marginTop: 4 }}>
            {t('profile.logoutText')}
          </p>
        </div>

        <button type="button" className="btn btn--danger" onClick={handleLogout}>
          <Icon name="logout" size={16} /> {t('profile.logout')}
        </button>
      </section>
    </main>
  );
}

// ===== Personal information =====

function PersonalInfo({ user }) {
  const { t } = useTranslation();
  const [form, setForm] = useState({ firstName: user.firstName, lastName: user.lastName });

  const mutation = useProfileMutation(() => profileApi.update(form));

  const updateField = (event) => {
    setForm({ ...form, [event.target.name]: event.target.value });
  };

  return (
    <form
      className={`card ${styles.cardBody}`}
      onSubmit={(event) => {
        event.preventDefault();
        mutation.mutate();
      }}
    >
      <h2 className={styles.sectionTitle}>{t('profile.personalInfo')}</h2>

      <ErrorAlert error={mutation.error} />
      {mutation.isSuccess && <div className="alert alert--success">{t('common.saved')}</div>}

      <div className="form-grid">
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

        <label className="field">
          {t('fields.email')}
          <input className="input" value={user.email} readOnly />
        </label>
      </div>

      <button
        type="submit"
        className="btn btn--primary"
        style={{ alignSelf: 'flex-end' }}
        disabled={mutation.isPending}
      >
        {t('profile.saveChanges')}
      </button>
    </form>
  );
}

// ===== Integrations =====

function TelegramCard({ user }) {
  const { t } = useTranslation();
  const [chatId, setChatId] = useState(user.telegramChatId ?? '');

  const mutation = useProfileMutation(() => profileApi.saveTelegram(Number(chatId)));

  return (
    <IntegrationCard
      icon={<Icon name="send" />}
      iconClass={styles.iconTelegram}
      title="Telegram"
      isConnected={Boolean(user.telegramChatId)}
    >
      <p className={styles.text}>{t('profile.telegramHint')}</p>

      <ErrorAlert error={mutation.error} />

      <label className="field">
        {t('profile.chatId')}
        <input
          className="input"
          inputMode="numeric"
          value={chatId}
          onChange={(event) => setChatId(event.target.value.replace(/\D/g, ''))}
        />
      </label>

      <button
        type="button"
        className="btn btn--ghost btn--small"
        disabled={!chatId || mutation.isPending}
        onClick={() => mutation.mutate()}
      >
        {mutation.isSuccess ? t('common.saved') : t('profile.saveChatId')}
      </button>
    </IntegrationCard>
  );
}

function EmailCard({ user }) {
  const { t } = useTranslation();
  const [form, setForm] = useState({
    emailUsername: user.emailUsername ?? '',
    applicationPassword: '',
  });

  const mutation = useProfileMutation(
    () => profileApi.saveEmailCredentials(form),
    () => setForm((current) => ({ ...current, applicationPassword: '' })),
  );

  return (
    <IntegrationCard
      icon={<Icon name="mail" />}
      iconClass={styles.iconEmail}
      title="Email"
      isConnected={user.emailConnected}
    >
      <ErrorAlert error={mutation.error} />

      <label className="field">
        {t('profile.gmailAddress')}
        <input
          className="input"
          type="email"
          value={form.emailUsername}
          onChange={(event) => setForm({ ...form, emailUsername: event.target.value })}
        />
      </label>

      <label className="field">
        {t('profile.appPassword')}
        <input
          className="input"
          type="password"
          autoComplete="new-password"
          placeholder={t('profile.appPasswordPlaceholder')}
          value={form.applicationPassword}
          onChange={(event) => setForm({ ...form, applicationPassword: event.target.value })}
        />
        <span className={styles.lockHint}>
          <Icon name="lock" size={13} /> {t('profile.storedEncrypted')}
        </span>
      </label>

      <button
        type="button"
        className="btn btn--ghost btn--small"
        disabled={!form.emailUsername || !form.applicationPassword || mutation.isPending}
        onClick={() => mutation.mutate()}
      >
        {mutation.isSuccess ? t('common.saved') : t('profile.saveEmail')}
      </button>
    </IntegrationCard>
  );
}

function CalendarCard({ user }) {
  const { t } = useTranslation();
  const [searchParams] = useSearchParams();

  // The backend redirects back here with ?calendar=connected or ?calendar=error
  const oauthResult = searchParams.get('calendar');

  const connectMutation = useMutation({
    mutationFn: profileApi.getCalendarConnectUrl,

    // Leave the app and go to the Google consent screen.
    onSuccess: ({ authorizationUrl }) => window.location.assign(authorizationUrl),
  });

  const disconnectMutation = useProfileMutation(profileApi.disconnectCalendar);

  return (
    <IntegrationCard
      icon={<Icon name="calendar" />}
      iconClass={styles.iconCalendar}
      title="Google Calendar"
      isConnected={user.googleCalendarConnected}
    >
      {oauthResult === 'connected' && (
        <div className="alert alert--success">{t('profile.calendarConnected')}</div>
      )}
      {oauthResult === 'error' && (
        <div className="alert alert--error">{t('profile.calendarError')}</div>
      )}

      <ErrorAlert error={connectMutation.error ?? disconnectMutation.error} />

      <p className={styles.text}>{t('profile.calendarHint')}</p>

      {user.googleCalendarConnected ? (
        <button
          type="button"
          className="btn btn--danger btn--small"
          disabled={disconnectMutation.isPending}
          onClick={() => disconnectMutation.mutate()}
        >
          {t('profile.disconnect')}
        </button>
      ) : (
        <button
          type="button"
          className="btn btn--primary btn--small"
          disabled={connectMutation.isPending}
          onClick={() => connectMutation.mutate()}
        >
          {t('profile.connectCalendar')}
        </button>
      )}
    </IntegrationCard>
  );
}

function DropboxCard() {
  const { t } = useTranslation();

  return (
    <IntegrationCard
      icon={<DropboxIcon />}
      iconClass={styles.iconDropbox}
      title="Dropbox"
      isConnected
    >
      <p className={styles.text}>{t('profile.dropboxHint')}</p>

      <Link to="/tasks" className="btn btn--ghost btn--small">
        {t('profile.uploadFromTask')}
      </Link>
    </IntegrationCard>
  );
}

// ===== Shared pieces =====

function IntegrationCard({ icon, iconClass, title, isConnected, children }) {
  const { t } = useTranslation();

  return (
    <article className={`card ${styles.cardBody}`}>
      <div className={styles.cardHeader}>
        <span className={styles.cardTitle}>
          <span className={`${styles.icon} ${iconClass}`}>{icon}</span>
          {title}
        </span>

        <span className={`${styles.connection} ${isConnected ? styles.connectionOn : ''}`}>
          <span className="dot" />
          {isConnected ? t('profile.connected') : t('profile.notConnected')}
        </span>
      </div>

      {children}
    </article>
  );
}

// Every profile change refreshes the profile, so badges and flags update everywhere.
function useProfileMutation(mutationFn, onSuccess) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn,

    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.profile });
      onSuccess?.();
    },
  });
}
