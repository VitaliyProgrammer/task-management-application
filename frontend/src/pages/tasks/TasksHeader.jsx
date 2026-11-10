import { NavLink } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

// Title + Board/List switch, shared by both task views.
export function TasksHeader({ subtitle, children }) {
  const { t } = useTranslation();

  const viewClass = ({ isActive }) => `segmented__item ${isActive ? 'segmented__item--active' : ''}`;

  return (
    <header className="page-header">
      <div className="toolbar" style={{ gap: 20 }}>
        <div>
          <h1 className="page-title">{t('nav.tasks')}</h1>
          <p className="page-subtitle">{subtitle}</p>
        </div>

        <nav className="segmented" aria-label={t('tasks.view')}>
          <NavLink to="/tasks" end className={viewClass}>
            {t('tasks.board')}
          </NavLink>

          <NavLink to="/tasks/list" className={viewClass}>
            {t('tasks.list')}
          </NavLink>
        </nav>
      </div>

      <div className="toolbar">{children}</div>
    </header>
  );
}
