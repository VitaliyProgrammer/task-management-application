import { useTranslation } from 'react-i18next';
import { getErrorMessages } from '../lib/errors';

// Loading / error / empty states, so every page handles them the same way.

export function LoadingState({ fullScreen = false }) {
  const { t } = useTranslation();

  return (
    <div
      className="state"
      style={fullScreen ? { minHeight: '100vh', paddingTop: '30vh' } : undefined}
    >
      <div className="spinner" />
      {t('common.loading')}
    </div>
  );
}

export function ErrorState({ error }) {
  const { t } = useTranslation();

  return (
    <div className="state">
      <ErrorAlert error={error} />
      <p style={{ marginTop: 12 }}>{t('common.tryAgainLater')}</p>
    </div>
  );
}

export function EmptyState({ children }) {
  return <div className="state">{children}</div>;
}

export function ErrorAlert({ error }) {
  const { t } = useTranslation();

  if (!error) {
    return null;
  }

  const messages = getErrorMessages(error, t);

  return (
    <div className="alert alert--error" role="alert">
      {messages.length === 1 ? (
        messages[0]
      ) : (
        <ul>
          {messages.map((message) => (
            <li key={message}>{message}</li>
          ))}
        </ul>
      )}
    </div>
  );
}
