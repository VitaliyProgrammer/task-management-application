import { useMemo, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { tasksApi } from '../../api/endpoints';
import { queryKeys, useProjectsById, useTasks, useUsers } from '../../hooks/queries';
import { Avatar, LabelChip, PriorityBadge } from '../../components/Badges';
import { ErrorAlert, ErrorState, LoadingState } from '../../components/States';
import { formatDate } from '../../lib/format';
import { TASK_STATUSES, boardColumnOf, statusStyle } from '../../lib/taskMeta';
import { TaskFormModal } from './TaskFormModal';
import { TasksHeader } from './TasksHeader';
import styles from './BoardPage.module.css';

export function BoardPage() {
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const [searchParams, setSearchParams] = useSearchParams();

  const tasksQuery = useTasks();
  const { usersById } = useUsers();
  const projectsById = useProjectsById();

  const [search, setSearch] = useState('');
  const [draggedTaskId, setDraggedTaskId] = useState(null);
  const [dropTarget, setDropTarget] = useState(null);

  // "?new=1" in the URL opens the "New task" dialog (used by the Dashboard button).
  const isFormOpen = searchParams.get('new') === '1';

  const tasks = useMemo(() => tasksQuery.data?.content ?? [], [tasksQuery.data]);

  const columns = useMemo(() => {
    const query = search.trim().toLowerCase();
    const visible = tasks.filter((task) => task.title.toLowerCase().includes(query));

    return TASK_STATUSES.map((status) => ({
      status,
      tasks: visible.filter((task) => boardColumnOf(task) === status),
    }));
  }, [tasks, search]);

  // Moving a card = updating the task with a new status.
  const moveMutation = useMutation({
    mutationFn: ({ task, status }) =>
      tasksApi.update(task.id, {
        title: task.title,
        description: task.description,
        taskPriority: task.taskPriority,
        taskStatus: status,
        dueDate: task.dueDate,
        assigneeId: task.assigneeId,
        labelIds: task.labels.map((label) => label.id),
      }),

    onSettled: () => queryClient.invalidateQueries({ queryKey: queryKeys.tasks }),
  });

  const handleDrop = (status) => {
    const task = tasks.find((item) => item.id === draggedTaskId);

    if (task && boardColumnOf(task) !== status) {
      moveMutation.mutate({ task, status });
    }

    setDraggedTaskId(null);
    setDropTarget(null);
  };

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

  return (
    <main className="page">
      <TasksHeader subtitle={t('tasks.count', { count: tasks.length })}>
        <input
          className="input"
          type="search"
          placeholder={t('tasks.searchPlaceholder')}
          aria-label={t('tasks.searchPlaceholder')}
          value={search}
          onChange={(event) => setSearch(event.target.value)}
          style={{ width: 220, borderRadius: 'var(--radius-pill)' }}
        />

        <Link to="/tasks/list" className="btn btn--ghost">
          {t('tasks.filters')}
        </Link>

        <button
          type="button"
          className="btn btn--primary"
          onClick={() => setSearchParams({ new: '1' })}
        >
          + {t('tasks.newTask')}
        </button>
      </TasksHeader>

      <ErrorAlert error={moveMutation.error} />

      <p className="field__hint">{t('tasks.dragHint')}</p>

      <section className={styles.board} aria-label={t('tasks.board')}>
        {columns.map((column) => (
          <div
            key={column.status}
            className={`${styles.column} ${dropTarget === column.status ? styles.columnDrop : ''}`}
            style={statusStyle(column.status)}
            onDragOver={(event) => {
              event.preventDefault();
              setDropTarget(column.status);
            }}
            onDragLeave={() => setDropTarget(null)}
            onDrop={() => handleDrop(column.status)}
          >
            <div className={styles.columnHeader}>
              <span className="dot" style={{ color: 'var(--pill-dot)' }} />
              <h2 className={styles.columnTitle}>{t(`taskStatus.${column.status}`)}</h2>
            </div>

            {column.tasks.map((task) => (
              <TaskCard
                key={task.id}
                task={task}
                assignee={usersById.get(task.assigneeId)}
                projectName={projectsById.get(task.projectId)?.name}
                isDragging={draggedTaskId === task.id}
                onDragStart={() => setDraggedTaskId(task.id)}
                onDragEnd={() => setDraggedTaskId(null)}
              />
            ))}

            {column.tasks.length === 0 && (
              <p className={styles.emptyColumn}>{t('tasks.emptyColumn')}</p>
            )}
          </div>
        ))}
      </section>

      {isFormOpen && <TaskFormModal onClose={() => setSearchParams({})} />}
    </main>
  );
}

function TaskCard({ task, assignee, projectName, isDragging, onDragStart, onDragEnd }) {
  const { t, i18n } = useTranslation();

  return (
    <Link
      to={`/tasks/${task.id}`}
      className={`${styles.card} ${isDragging ? styles.cardDragging : ''}`}
      draggable
      onDragStart={onDragStart}
      onDragEnd={onDragEnd}
    >
      <span className={styles.cardTop}>
        <span className={styles.labels}>
          {task.labels.slice(0, 2).map((label) => (
            <LabelChip key={label.id} label={label} />
          ))}
        </span>

        <PriorityBadge priority={task.taskPriority} />
      </span>

      <span className={styles.cardTitle}>{task.title}</span>

      <span className={styles.cardMeta}>
        <span>
          {projectName}
          {task.dueDate &&
            ` · ${t('tasks.due')} ${formatDate(task.dueDate, i18n.language, { withYear: false })}`}
        </span>

        {assignee && <Avatar user={assignee} size={28} />}
      </span>
    </Link>
  );
}
