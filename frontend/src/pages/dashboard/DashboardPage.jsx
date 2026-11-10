import { useMemo } from 'react';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../../auth/AuthContext';
import { useProjects, useTasks, useUsers } from '../../hooks/queries';
import { Avatar, StatusPill } from '../../components/Badges';
import { ErrorState, LoadingState } from '../../components/States';
import { daysFromToday, formatDate } from '../../lib/format';
import { TASK_PRIORITIES, TASK_STATUSES, boardColumnOf, statusStyle } from '../../lib/taskMeta';
import styles from './DashboardPage.module.css';

const UPCOMING_LIMIT = 5;
const DAYS_IN_WEEK = 7;

export function DashboardPage() {
  const { t, i18n } = useTranslation();
  const { user } = useAuth();

  const tasksQuery = useTasks();
  const { projects } = useProjects();
  const { usersById } = useUsers();

  const tasks = useMemo(() => tasksQuery.data?.content ?? [], [tasksQuery.data]);

  // All numbers on the page are derived from the same task list.
  const stats = useMemo(() => buildStats(tasks), [tasks]);

  if (tasksQuery.isPending) {
    return (
      <main className="page">
        <LoadingState />
      </main>
    );
  }

  if (tasksQuery.isError) {
    return (
      <main className="page">
        <ErrorState error={tasksQuery.error} />
      </main>
    );
  }

  const maxPriorityCount = Math.max(1, ...Object.values(stats.byPriority));

  return (
    <main className="page">
      <header className="page-header">
        <div>
          <h1 className="page-title">{t('dashboard.title')}</h1>
          <p className="page-subtitle">{t('dashboard.greeting', { name: user.firstName })}</p>
        </div>

        <Link to="/tasks?new=1" className="btn btn--primary">
          + {t('tasks.newTask')}
        </Link>
      </header>

      {/* ===== Summary cards ===== */}
      <section className={styles.statsGrid} aria-label={t('dashboard.statistics')}>
        <StatCard label={t('dashboard.totalTasks')} value={stats.total} />
        <StatCard label={t('taskStatus.IN_PROGRESS')} value={stats.byStatus.IN_PROGRESS} />
        <StatCard label={t('taskStatus.COMPLETED')} value={stats.byStatus.COMPLETED} />
        <StatCard label={t('dashboard.dueThisWeek')} value={stats.dueThisWeek} />
      </section>

      <div className={styles.twoColumns}>
        {/* ===== Tasks by status ===== */}
        <section className="card">
          <h2 className="card__title">{t('dashboard.byStatus')}</h2>

          <div className={styles.statusBar} role="img" aria-label={t('dashboard.byStatus')}>
            {TASK_STATUSES.filter((status) => stats.byStatus[status] > 0).map((status, index) => (
              <span
                key={status}
                className={styles.statusSegment}
                style={{
                  flex: stats.byStatus[status],
                  animationDelay: `${index * 0.15}s`,
                  ...statusStyle(status),
                }}
              />
            ))}
          </div>

          <ul className={styles.legend}>
            {TASK_STATUSES.map((status) => (
              <li key={status} className={styles.legendRow} style={statusStyle(status)}>
                <span className="dot" style={{ color: 'var(--pill-dot)' }} />
                <span className={styles.legendName}>{t(`taskStatus.${status}`)}</span>
                <strong>{stats.byStatus[status]}</strong>
              </li>
            ))}
          </ul>
        </section>

        {/* ===== Tasks by priority ===== */}
        <section className="card">
          <h2 className="card__title">{t('dashboard.byPriority')}</h2>

          <div className={styles.priorityList}>
            {TASK_PRIORITIES.map((priority) => (
              <div key={priority} className={styles.priorityRow}>
                <div className={styles.priorityHeader}>
                  <span className={styles[`ink${priority}`]}>{t(`priority.${priority}`)}</span>
                  <span>{stats.byPriority[priority]}</span>
                </div>

                <div className={styles.track}>
                  <div
                    className={`${styles.fill} ${styles[`fill${priority}`]}`}
                    style={{ width: `${(stats.byPriority[priority] / maxPriorityCount) * 100}%` }}
                  />
                </div>
              </div>
            ))}
          </div>
        </section>
      </div>

      <div className={styles.twoColumns}>
        {/* ===== Upcoming deadlines ===== */}
        <section className="card">
          <div className={styles.cardHeader}>
            <h2 className="card__title">{t('dashboard.upcoming')}</h2>
            <Link to="/tasks/list">{t('common.viewAll')}</Link>
          </div>

          {stats.upcoming.length === 0 && (
            <p className="field__hint">{t('dashboard.noDeadlines')}</p>
          )}

          {stats.upcoming.map((task) => (
            <Link key={task.id} to={`/tasks/${task.id}`} className={styles.listRow}>
              <Avatar user={usersById.get(task.assigneeId)} size={34} />

              <span className={styles.listText}>
                <span className={styles.listTitle}>{task.title}</span>
                <span className="field__hint">
                  {projects.find((p) => p.id === task.projectId)?.name}
                </span>
              </span>

              <DueLabel date={task.dueDate} language={i18n.language} />
            </Link>
          ))}
        </section>

        {/* ===== Projects ===== */}
        <section className="card">
          <div className={styles.cardHeader}>
            <h2 className="card__title">{t('nav.projects')}</h2>
            <Link to="/projects">{t('common.viewAll')}</Link>
          </div>

          {projects.slice(0, UPCOMING_LIMIT).map((project) => (
            <div key={project.id} className={styles.listRow}>
              <span className={styles.listText}>
                <span className={styles.listTitle}>{project.name}</span>
                <span className="field__hint">
                  {t('tasks.count', { count: stats.tasksPerProject.get(project.id) ?? 0 })}
                  {project.endDate &&
                    ` · ${t('projects.ends')} ${formatDate(project.endDate, i18n.language)}`}
                </span>
              </span>

              <StatusPill status={project.status} kind="project" />
            </div>
          ))}
        </section>
      </div>
    </main>
  );
}

function StatCard({ label, value }) {
  return (
    <div className={styles.statCard}>
      <div className={styles.statLabel}>{label}</div>
      <div className={styles.statValue}>{value}</div>
    </div>
  );
}

function DueLabel({ date, language }) {
  const { t } = useTranslation();
  const days = daysFromToday(date);

  if (days < 0) {
    return <span className={styles.dueOverdue}>{t('dashboard.overdue')}</span>;
  }

  if (days === 0) {
    return <span className={styles.dueSoon}>{t('dashboard.today')}</span>;
  }

  if (days === 1) {
    return <span className={styles.dueSoon}>{t('dashboard.tomorrow')}</span>;
  }

  return <span className={styles.due}>{formatDate(date, language, { withYear: false })}</span>;
}

// Pure function: easy to read and easy to unit test later.
function buildStats(tasks) {
  const byStatus = { NOT_STARTED: 0, IN_PROGRESS: 0, COMPLETED: 0 };
  const byPriority = { HIGH: 0, MEDIUM: 0, LOW: 0 };
  const tasksPerProject = new Map();

  let dueThisWeek = 0;

  for (const task of tasks) {
    byStatus[boardColumnOf(task)] += 1;

    if (task.taskPriority in byPriority) {
      byPriority[task.taskPriority] += 1;
    }

    tasksPerProject.set(task.projectId, (tasksPerProject.get(task.projectId) ?? 0) + 1);

    const days = daysFromToday(task.dueDate);

    if (task.taskStatus !== 'COMPLETED' && days !== null && days >= 0 && days <= DAYS_IN_WEEK) {
      dueThisWeek += 1;
    }
  }

  const upcoming = tasks
    .filter((task) => task.taskStatus !== 'COMPLETED' && task.dueDate)
    .sort((a, b) => a.dueDate.localeCompare(b.dueDate))
    .slice(0, UPCOMING_LIMIT);

  return { total: tasks.length, byStatus, byPriority, tasksPerProject, dueThisWeek, upcoming };
}
