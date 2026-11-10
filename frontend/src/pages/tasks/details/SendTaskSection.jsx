import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useMutation } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../../../auth/AuthContext';
import { notificationsApi } from '../../../api/endpoints';
import { ErrorAlert } from '../../../components/States';
import { Icon } from '../../../components/Icon';
import { toInputDate } from '../../../lib/format';
import styles from './details.module.css';

// Send the task to Telegram / Email (administrators) or to your own Google Calendar.
export function SendTaskSection({ task, assignee }) {
  const { t } = useTranslation();
  const { user, isAdmin } = useAuth();

  const [email, setEmail] = useState('');
  const [dates, setDates] = useState({
    startDate: toInputDate(new Date()),
    endDate: task.dueDate ?? toInputDate(new Date()),
  });
  const [successMessage, setSuccessMessage] = useState('');

  const onSent = (messageKey) => () => setSuccessMessage(t(messageKey));

  const telegramMutation = useMutation({
    mutationFn: () => notificationsApi.telegram(task.id),
    onSuccess: onSent('send.telegramSent'),
  });

  const emailMutation = useMutation({
    mutationFn: () => notificationsApi.email(task.id, email),
    onSuccess: onSent('send.emailSent'),
  });

  const calendarMutation = useMutation({
    mutationFn: () => notificationsApi.calendar(task.id, dates),
    onSuccess: onSent('send.calendarSent'),
  });

  const error = telegramMutation.error ?? emailMutation.error ?? calendarMutation.error;

  return (
    <section className="card">
      <div className={styles.sectionHeader}>
        <h2 className="card__title">{t('send.title')}</h2>
      </div>

      <ErrorAlert error={error} />
      {successMessage && <div className="alert alert--success">{successMessage}</div>}

      {isAdmin && (
        <>
          <button
            type="button"
            className="btn btn--ghost"
            onClick={() => telegramMutation.mutate()}
            disabled={telegramMutation.isPending || !assignee}
            title={!assignee ? t('send.needsAssignee') : undefined}
          >
            <Icon name="send" style={{ color: 'var(--status-in-progress-dot)' }} />
            {t('send.telegram')}
          </button>

          <form
            className={styles.inlineForm}
            onSubmit={(event) => {
              event.preventDefault();
              emailMutation.mutate();
            }}
          >
            <label className="field">
              {t('fields.email')}
              <span className={styles.inlineRow}>
                <input
                  className="input"
                  type="email"
                  placeholder="recipient@example.com"
                  value={email}
                  onChange={(event) => setEmail(event.target.value)}
                  required
                />
                <button
                  type="submit"
                  className="btn btn--primary btn--small"
                  disabled={emailMutation.isPending}
                >
                  {t('send.send')}
                </button>
              </span>
            </label>
          </form>
        </>
      )}

      <div className="field">
        {t('send.calendar')}

        {user.googleCalendarConnected ? (
          <>
            <span className={styles.inlineRow}>
              <input
                className="input"
                type="date"
                aria-label={t('send.startDate')}
                value={dates.startDate}
                onChange={(event) => setDates({ ...dates, startDate: event.target.value })}
              />
              <input
                className="input"
                type="date"
                aria-label={t('send.endDate')}
                value={dates.endDate}
                onChange={(event) => setDates({ ...dates, endDate: event.target.value })}
              />
            </span>

            <button
              type="button"
              className="btn btn--ghost btn--small"
              onClick={() => calendarMutation.mutate()}
              disabled={calendarMutation.isPending}
            >
              <Icon name="calendar" size={16} /> {t('send.addToCalendar')}
            </button>
          </>
        ) : (
          <span className="field__hint">
            {t('send.calendarNotConnected')} <Link to="/profile">{t('nav.profile')}</Link>
          </span>
        )}
      </div>
    </section>
  );
}
