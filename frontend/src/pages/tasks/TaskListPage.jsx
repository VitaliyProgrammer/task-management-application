import { useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useProjects, useProjectsById, useTasks, useUsers } from '../../hooks/queries';
import { Avatar, PriorityBadge, StatusPill } from '../../components/Badges';
import { EmptyState, ErrorState, LoadingState } from '../../components/States';
import { Icon } from '../../components/Icon';
import { formatDate } from '../../lib/format';
import { fullName } from '../../lib/people';
import { TASK_PRIORITIES, TASK_STATUSES, priorityStyle, statusStyle } from '../../lib/taskMeta';
import { TasksHeader } from './TasksHeader';
import { TaskFormModal } from './TaskFormModal';
import styles from './TaskListPage.module.css';

const PAGE_SIZE = 10;

const EMPTY_FILTERS = {
  title: '',
  project: '',
  assignee: '',
  dueFrom: '',
  dueTo: '',
  status: [],
  priority: [],
  hasAttachment: false,
};

// Turns the filter form into query params the backend understands.
// Empty values are skipped, so they do not filter anything.
function toSearchParams(filters, page) {
  const params = { page, size: PAGE_SIZE, sort: 'dueDate' };

  for (const key of ['title', 'project', 'assignee', 'dueFrom', 'dueTo']) {
    if (filters[key]) {
      params[key] = filters[key];
    }
  }

  if (filters.status.length) {
    params.status = filters.status;
  }

  if (filters.priority.length) {
    params.priority = filters.priority;
  }

  if (filters.hasAttachment) {
    params.hasAttachment = true;
  }

  return params;
}

export function TaskListPage() {
  const { t, i18n } = useTranslation();

  // "/tasks/list?project=3" (from a project card) starts with that project selected.
  const [searchParams] = useSearchParams();
  const initialFilters = { ...EMPTY_FILTERS, project: searchParams.get('project') ?? '' };

  // "draft" is what the user is typing, "applied" is what we actually search for.
  const [draft, setDraft] = useState(initialFilters);
  const [applied, setApplied] = useState(initialFilters);
  const [page, setPage] = useState(0);
  const [isFormOpen, setIsFormOpen] = useState(false);

  const tasksQuery = useTasks(toSearchParams(applied, page));
  const { projects } = useProjects();
  const projectsById = useProjectsById();
  const { users, usersById } = useUsers();

  const updateDraft = (event) => {
    const { name, type, value, checked } = event.target;
    setDraft({ ...draft, [name]: type === 'checkbox' ? checked : value });
  };

  const toggleInList = (key, value) => {
    const list = draft[key].includes(value)
      ? draft[key].filter((item) => item !== value)
      : [...draft[key], value];

    setDraft({ ...draft, [key]: list });
  };

  const applyFilters = (event) => {
    event.preventDefault();
    setApplied(draft);
    setPage(0);
  };

  const resetFilters = () => {
    setDraft(EMPTY_FILTERS);
    setApplied(EMPTY_FILTERS);
    setPage(0);
  };

  const result = tasksQuery.data;
  const tasks = result?.content ?? [];

  return (
    <main className="page">
      <TasksHeader subtitle={t('tasks.results', { count: result?.totalElements ?? 0 })}>
        <button type="button" className="btn btn--primary" onClick={() => setIsFormOpen(true)}>
          + {t('tasks.newTask')}
        </button>
      </TasksHeader>

      {/* ===== Filters ===== */}
      <form
        className={`card ${styles.filters}`}
        onSubmit={applyFilters}
        aria-label={t('tasks.filters')}
      >
        <div className={styles.filterRow}>
          <label className="field">
            {t('fields.title')}
            <input
              className="input"
              type="search"
              name="title"
              value={draft.title}
              onChange={updateDraft}
            />
          </label>

          <label className="field">
            {t('fields.project')}
            <select className="input" name="project" value={draft.project} onChange={updateDraft}>
              <option value="">{t('tasks.allProjects')}</option>
              {projects.map((project) => (
                <option key={project.id} value={project.id}>
                  {project.name}
                </option>
              ))}
            </select>
          </label>

          <label className="field">
            {t('fields.assignee')}
            <select className="input" name="assignee" value={draft.assignee} onChange={updateDraft}>
              <option value="">{t('tasks.anyone')}</option>
              {users.map((user) => (
                <option key={user.id} value={user.id}>
                  {fullName(user)}
                </option>
              ))}
            </select>
          </label>

          <div className={`field ${styles.dateField}`}>
            {t('tasks.dueBetween')}
            <span className={styles.dateRange}>
              <input
                className="input"
                type="date"
                name="dueFrom"
                aria-label={t('tasks.dueFrom')}
                value={draft.dueFrom}
                onChange={updateDraft}
              />
              <input
                className="input"
                type="date"
                name="dueTo"
                aria-label={t('tasks.dueTo')}
                value={draft.dueTo}
                onChange={updateDraft}
              />
            </span>
          </div>
        </div>

        <div className={styles.chipsRow}>
          <span className={styles.chipsLabel}>{t('fields.status')}</span>

          {TASK_STATUSES.map((status) => {
            const isOn = draft.status.includes(status);

            return (
              <button
                key={status}
                type="button"
                aria-pressed={isOn}
                className={`${styles.filterChip} ${isOn ? styles.filterChipOn : ''}`}
                style={statusStyle(status)}
                onClick={() => toggleInList('status', status)}
              >
                <span className="dot" style={{ color: 'var(--pill-dot)' }} />
                {t(`taskStatus.${status}`)}
              </button>
            );
          })}

          <span className={styles.divider} />
          <span className={styles.chipsLabel}>{t('fields.priority')}</span>

          {TASK_PRIORITIES.map((priority) => {
            const isOn = draft.priority.includes(priority);

            return (
              <button
                key={priority}
                type="button"
                aria-pressed={isOn}
                className={`${styles.filterChip} ${isOn ? styles.priorityOn : ''}`}
                style={priorityStyle(priority)}
                onClick={() => toggleInList('priority', priority)}
              >
                {t(`priority.${priority}`)}
              </button>
            );
          })}

          <label className={styles.checkbox}>
            <input
              type="checkbox"
              name="hasAttachment"
              checked={draft.hasAttachment}
              onChange={updateDraft}
            />
            {t('tasks.hasAttachment')}
          </label>

          <span className={styles.spacer} />

          <button type="button" className="btn btn--ghost btn--small" onClick={resetFilters}>
            {t('common.reset')}
          </button>

          <button type="submit" className="btn btn--primary btn--small">
            {t('tasks.applyFilters')}
          </button>
        </div>
      </form>

      {/* ===== Results ===== */}
      {tasksQuery.isPending && <LoadingState />}
      {tasksQuery.isError && <ErrorState error={tasksQuery.error} />}

      {tasksQuery.isSuccess && tasks.length === 0 && (
        <EmptyState>{t('tasks.nothingFound')}</EmptyState>
      )}

      {tasksQuery.isSuccess && tasks.length > 0 && (
        <div className="table-box">
          <table className="table" style={{ minWidth: 760 }}>
            <thead>
              <tr>
                <th>{t('fields.title')}</th>
                <th>{t('fields.status')}</th>
                <th>{t('fields.priority')}</th>
                <th>{t('fields.project')}</th>
                <th>{t('fields.assignee')}</th>
                <th>{t('fields.dueDate')}</th>
              </tr>
            </thead>

            <tbody>
              {tasks.map((task) => {
                const assignee = usersById.get(task.assigneeId);

                return (
                  <tr key={task.id}>
                    <td>
                      <Link to={`/tasks/${task.id}`} className={styles.titleLink}>
                        {task.title}
                      </Link>
                    </td>
                    <td>
                      <StatusPill status={task.taskStatus} />
                    </td>
                    <td>
                      <PriorityBadge priority={task.taskPriority} />
                    </td>
                    <td className="field__hint">{projectsById.get(task.projectId)?.name}</td>
                    <td>
                      {assignee ? (
                        <span className={styles.person}>
                          <Avatar user={assignee} size={28} />
                          {fullName(assignee)}
                        </span>
                      ) : (
                        <span className="field__hint">{t('tasks.unassigned')}</span>
                      )}
                    </td>
                    <td className={styles.nowrap}>{formatDate(task.dueDate, i18n.language)}</td>
                  </tr>
                );
              })}
            </tbody>
          </table>

          <div className={styles.pagination}>
            <span className="field__hint">
              {t('tasks.pageOf', {
                page: (result.number ?? 0) + 1,
                total: Math.max(1, result.totalPages ?? 1),
              })}
            </span>

            <div className="toolbar" style={{ gap: 6 }}>
              <button
                type="button"
                className="btn btn--ghost btn--small"
                disabled={page === 0}
                onClick={() => setPage(page - 1)}
              >
                <Icon name="chevronDown" style={{ transform: 'rotate(90deg)' }} />{' '}
                {t('common.previous')}
              </button>

              <button
                type="button"
                className="btn btn--ghost btn--small"
                disabled={page + 1 >= (result.totalPages ?? 1)}
                onClick={() => setPage(page + 1)}
              >
                {t('common.next')}{' '}
                <Icon name="chevronDown" style={{ transform: 'rotate(-90deg)' }} />
              </button>
            </div>
          </div>
        </div>
      )}

      {isFormOpen && <TaskFormModal onClose={() => setIsFormOpen(false)} />}
    </main>
  );
}
