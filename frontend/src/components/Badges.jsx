import { useTranslation } from 'react-i18next';
import { priorityStyle, statusStyle } from '../lib/taskMeta';
import { avatarColors, fullName, initials } from '../lib/people';

// Small presentational pieces used across many pages.

export function StatusPill({ status, kind = 'task' }) {
  const { t } = useTranslation();

  return (
    <span className="status-pill" style={statusStyle(status)}>
      <span className="dot" />
      {t(`${kind}Status.${status ?? 'NO_SPECIFIED'}`)}
    </span>
  );
}

export function PriorityBadge({ priority }) {
  const { t } = useTranslation();

  if (!priority || priority === 'NO_SPECIFIED') {
    return null;
  }

  return (
    <span className="badge" style={priorityStyle(priority)}>
      {t(`priority.${priority}`)}
    </span>
  );
}

export function LabelChip({ label }) {
  return (
    <span className="chip">
      <span className="chip__swatch" style={{ background: label.color }} />
      {label.name}
    </span>
  );
}

export function Avatar({ user, size = 32 }) {
  const colors = avatarColors(user);

  return (
    <span
      className="avatar"
      title={fullName(user)}
      style={{
        width: size,
        height: size,
        fontSize: Math.round(size * 0.36),
        background: colors.bg,
        color: colors.fg,
      }}
    >
      {initials(user)}
    </span>
  );
}
