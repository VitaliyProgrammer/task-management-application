import { useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../../auth/AuthContext';
import { projectsApi } from '../../api/endpoints';
import { queryKeys, useProjects, useTasks } from '../../hooks/queries';
import { StatusPill } from '../../components/Badges';
import { ConfirmDialog } from '../../components/Modal';
import { EmptyState, ErrorAlert, ErrorState, LoadingState } from '../../components/States';
import { Icon } from '../../components/Icon';
import { formatDate } from '../../lib/format';
import { ProjectFormModal } from './ProjectFormModal';
import styles from './ProjectsPage.module.css';

export function ProjectsPage() {
  const { t, i18n } = useTranslation();
  const { isAdmin } = useAuth();
  const queryClient = useQueryClient();

  const projectsQuery = useProjects();
  const tasksQuery = useTasks();

  // null = closed, {} = new project, {project} = edit
  const [editing, setEditing] = useState(null);
  const [deleting, setDeleting] = useState(null);

  const tasksPerProject = useMemo(() => {
    const counts = new Map();

    for (const task of tasksQuery.data?.content ?? []) {
      counts.set(task.projectId, (counts.get(task.projectId) ?? 0) + 1);
    }

    return counts;
  }, [tasksQuery.data]);

  const deleteMutation = useMutation({
    mutationFn: (project) => projectsApi.remove(project.id),

    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.projects });
      queryClient.invalidateQueries({ queryKey: queryKeys.tasks });
      setDeleting(null);
    },
  });

  const { projects } = projectsQuery;

  const dateRange = (project) => {
    const start = formatDate(project.startDate, i18n.language);

    return project.endDate ? `${start} – ${formatDate(project.endDate, i18n.language)}` : start;
  };

  return (
    <main className="page">
      <header className="page-header">
        <div>
          <h1 className="page-title">{t('nav.projects')}</h1>
          <p className="page-subtitle">{t('projects.count', { count: projects.length })}</p>
        </div>

        {isAdmin && (
          <button type="button" className="btn btn--primary" onClick={() => setEditing({})}>
            + {t('projects.newProject')}
          </button>
        )}
      </header>

      <ErrorAlert error={deleteMutation.error} />

      {projectsQuery.isPending && <LoadingState />}
      {projectsQuery.isError && <ErrorState error={projectsQuery.error} />}
      {projectsQuery.isSuccess && projects.length === 0 && (
        <EmptyState>{t('projects.empty')}</EmptyState>
      )}

      <section className={styles.grid}>
        {projects.map((project) => (
          <article key={project.id} className={`card card--interactive ${styles.card}`}>
            <div className={styles.cardTop}>
              <StatusPill status={project.status} kind="project" />

              {isAdmin && (
                <span className="toolbar" style={{ gap: 6 }}>
                  <button
                    type="button"
                    className="icon-btn"
                    aria-label={`${t('common.edit')} ${project.name}`}
                    onClick={() => setEditing({ project })}
                  >
                    <Icon name="edit" size={16} />
                  </button>

                  <button
                    type="button"
                    className="icon-btn icon-btn--danger"
                    aria-label={`${t('common.delete')} ${project.name}`}
                    onClick={() => setDeleting(project)}
                  >
                    <Icon name="trash" size={16} />
                  </button>
                </span>
              )}
            </div>

            <div>
              <h2 className={styles.name}>{project.name}</h2>
              <p className={styles.description}>{project.description}</p>
            </div>

            <span className={styles.dates}>
              <Icon name="calendar" size={16} /> {dateRange(project)}
            </span>

            <Link to={`/tasks/list?project=${project.id}`} className={styles.tasksLink}>
              {t('tasks.count', { count: tasksPerProject.get(project.id) ?? 0 })}
              <span aria-hidden="true">→</span>
            </Link>
          </article>
        ))}
      </section>

      {editing && <ProjectFormModal project={editing.project} onClose={() => setEditing(null)} />}

      {deleting && (
        <ConfirmDialog
          title={t('projects.deleteTitle')}
          message={t('projects.deleteMessage', { name: deleting.name })}
          isPending={deleteMutation.isPending}
          onConfirm={() => deleteMutation.mutate(deleting)}
          onCancel={() => setDeleting(null)}
        />
      )}
    </main>
  );
}
