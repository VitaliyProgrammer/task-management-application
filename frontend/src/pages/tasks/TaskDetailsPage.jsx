import { useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../../auth/AuthContext';
import { tasksApi } from '../../api/endpoints';
import { queryKeys, useProjectsById, useTask, useUsers } from '../../hooks/queries';
import { LabelChip, PriorityBadge, StatusPill } from '../../components/Badges';
import { ConfirmDialog } from '../../components/Modal';
import { ErrorAlert, ErrorState, LoadingState } from '../../components/States';
import { Icon } from '../../components/Icon';
import { formatDate, formatDateTime } from '../../lib/format';
import { fullName } from '../../lib/people';
import { TaskFormModal } from './TaskFormModal';
import { CommentsSection } from './details/CommentsSection';
import { AttachmentsSection } from './details/AttachmentsSection';
import { SendTaskSection } from './details/SendTaskSection';
import styles from './TaskDetailsPage.module.css';

export function TaskDetailsPage() {
  const { taskId } = useParams();
  const { t, i18n } = useTranslation();
  const { isAdmin } = useAuth();
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const taskQuery = useTask(taskId);
  const { usersById } = useUsers();
  const projectsById = useProjectsById();

  const [isEditing, setIsEditing] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);

  const deleteMutation = useMutation({
    mutationFn: () => tasksApi.remove(taskId),

    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.tasks });
      navigate('/tasks', { replace: true });
    },
  });

  if (taskQuery.isPending) {
    return (
      <main className="page">
        <LoadingState />
      </main>
    );
  }

  if (taskQuery.isError) {
    return (
      <main className="page">
        <ErrorState error={taskQuery.error} />
      </main>
    );
  }

  const task = taskQuery.data;
  const assignee = usersById.get(task.assigneeId);

  const details = [
    { key: 'project', value: projectsById.get(task.projectId)?.name ?? '—' },
    { key: 'assignee', value: assignee ? fullName(assignee) : t('tasks.unassigned') },
    { key: 'dueDate', value: formatDate(task.dueDate, i18n.language) },
    { key: 'created', value: formatDateTime(task.createdAt, i18n.language) },
  ];

  return (
    <main className="page">
      <nav aria-label={t('common.breadcrumb')} className={styles.breadcrumb}>
        <Link to="/tasks">{t('nav.tasks')}</Link> / {task.title}
      </nav>

      <header className="page-header" style={{ alignItems: 'flex-start' }}>
        <div className={styles.titleBlock}>
          <h1 className="page-title">{task.title}</h1>

          <div className="toolbar" style={{ gap: 8 }}>
            <StatusPill status={task.taskStatus} />
            <PriorityBadge priority={task.taskPriority} />
          </div>
        </div>

        <div className="toolbar">
          <button type="button" className="btn btn--ghost" onClick={() => setIsEditing(true)}>
            <Icon name="edit" size={16} /> {t('common.edit')}
          </button>

          {isAdmin && (
            <button type="button" className="btn btn--danger" onClick={() => setIsDeleting(true)}>
              <Icon name="trash" size={16} /> {t('common.delete')}
            </button>
          )}
        </div>
      </header>

      <ErrorAlert error={deleteMutation.error} />

      <div className={styles.layout}>
        {/* ===== Main column ===== */}
        <div className={styles.mainColumn}>
          <section className="card">
            <h2 className="card__title">{t('fields.description')}</h2>
            <p className={styles.description}>{task.description || t('tasks.noDescription')}</p>
          </section>

          <CommentsSection taskId={task.id} usersById={usersById} />

          <AttachmentsSection taskId={task.id} />
        </div>

        {/* ===== Side column ===== */}
        <div className={styles.sideColumn}>
          <section className="card">
            <h2 className="card__title">{t('tasks.details')}</h2>

            <dl className={styles.detailsList}>
              {details.map((item) => (
                <div key={item.key} className={styles.detailsRow}>
                  <dt>{t(`fields.${item.key}`)}</dt>
                  <dd>{item.value}</dd>
                </div>
              ))}
            </dl>

            {task.labels.length > 0 && (
              <div className="toolbar" style={{ gap: 6 }}>
                {task.labels.map((label) => (
                  <LabelChip key={label.id} label={label} />
                ))}
              </div>
            )}
          </section>

          <SendTaskSection task={task} assignee={assignee} />
        </div>
      </div>

      {isEditing && <TaskFormModal task={task} onClose={() => setIsEditing(false)} />}

      {isDeleting && (
        <ConfirmDialog
          title={t('tasks.deleteTitle')}
          message={t('tasks.deleteMessage', { title: task.title })}
          isPending={deleteMutation.isPending}
          onConfirm={() => deleteMutation.mutate()}
          onCancel={() => setIsDeleting(false)}
        />
      )}
    </main>
  );
}
