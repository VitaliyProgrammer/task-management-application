import { Link, NavLink } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../../auth/AuthContext';
import { Icon } from '../Icon';
import { Logo } from '../Logo';
import { LanguageSwitcher } from '../LanguageSwitcher';
import styles from './Sidebar.module.css';

// Order approved in the design: working areas first, profile last.
const NAV_ITEMS = [
  { to: '/dashboard', icon: 'dashboard', label: 'nav.dashboard' },
  { to: '/tasks', icon: 'tasks', label: 'nav.tasks' },
  { to: '/projects', icon: 'projects', label: 'nav.projects' },
  { to: '/labels', icon: 'labels', label: 'nav.labels' },
  { to: '/users', icon: 'users', label: 'nav.users', adminOnly: true },
  { to: '/profile', icon: 'profile', label: 'nav.profile' },
];

export function Sidebar() {
  const { t } = useTranslation();
  const { isAdmin } = useAuth();

  const items = NAV_ITEMS.filter((item) => !item.adminOnly || isAdmin);

  return (
    <aside className={styles.sidebar}>
      <Link to="/" className={styles.brand} aria-label={t('nav.home')}>
        <Logo />
      </Link>

      <nav className={styles.nav} aria-label={t('nav.main')}>
        {items.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            className={({ isActive }) => `${styles.link} ${isActive ? styles.active : ''}`}
          >
            <Icon name={item.icon} />
            {t(item.label)}
            {item.adminOnly && <span className={styles.adminBadge}>ADMIN</span>}
          </NavLink>
        ))}
      </nav>

      <LanguageSwitcher className={styles.language} />
    </aside>
  );
}
