import { useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { tasksApi } from '../../api/endpoints';
import { queryKeys, useLabels, useProjects, useUsers } from '../../hooks/queries';
import { Modal } from '../../components/Modal';
import { ErrorAlert } from '../../components/States';
import { TASK_PRIORITIES, TASK_STATUSES, priorityStyle } from '../../lib/taskMeta';
import { fullName } from '../../lib/people';
import styles from './TaskFormModal.module.css';

// Builds the initial form state: an empty form, or the values of the edited task.
function initialForm(task) {
  return {
    title: task?.title ?? '',
    description: task?.description ?? '',
    projectId: task?.projectId ?? '',
    assigneeId: task?.assigneeId ?? '',
    taskStatus:
      task?.taskStatus && task.taskStatus !== 'NO_SPECIFIED' ? task.taskStatus : 'NOT_STARTED',
    taskPriority:
      task?.taskPriority && task.taskPriority !== 'NO_SPECIFIED' ? task.taskPriority : 'MEDIUM',
    dueDate: task?.dueDate ?? '',
    labelIds: task?.labels?.map((label) => label.id) ?? [],
  };
}

// Converts form strings into the shape the API expects.
function toRequest(form) {
  return {
    title: form.title.trim(),
    description: form.description.trim() || null,
    taskStatus: form.taskStatus,
    taskPriority: form.taskPriority,
    dueDate: form.dueDate || null,
    assigneeId: form.assigneeId ? Number(form.assigneeId) : null,
    labelIds: form.labelIds,
  };
}

export function TaskFormModal({ task, defaultProjectId, onClose, onSaved }) {
  const { t } = useTranslation();
  const queryClient = useQueryClient();

  const { projects } = useProjects();
  const { users } = useUsers();
  const { labels } = useLabels();

  const isEdit = Boolean(task);

  const [form, setForm] = useState(() => ({
    ...initialForm(task),
    ...(!task && defaultProjectId && { projectId: defaultProjectId }),
  }));

  const saveMutation = useMutation({
    mutationFn: () =>
      isEdit
        ? tasksApi.update(task.id, toRequest(form))
        : tasksApi.create({ ...toRequest(form), projectId: Number(form.projectId) }),

    onSuccess: (savedTask) => {
      queryClient.invalidateQueries({ queryKey: queryKeys.tasks });
      onSaved?.(savedTask);
      onClose();
    },
  });

  const updateField = (event) => {
    setForm({ ...form, [event.target.name]: event.target.value });
  };

  const toggleLabel = (labelId) => {
    const labelIds = form.labelIds.includes(labelId)
      ? form.labelIds.filter((id) => id !== labelId)
      : [...form.labelIds, labelId];

    setForm({ ...form, labelIds });
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    saveMutation.mutate();
  };

  return (
    <Modal
      title={isEdit ? t('tasks.editTask') : t('tasks.newTask')}
      onClose={onClose}
      footer={
        <>
          <button type="button" className="btn btn--ghost" onClick={onClose}>
            {t('common.cancel')}
          </button>

          <button
            type="submit"
            form="task-form"
            className="btn btn--primary"
            disabled={saveMutation.isPending}
          >
            {isEdit ? t('common.save') : t('tasks.create')}
          </button>
        </>
      }
    >
      <form id="task-form" onSubmit={handleSubmit} className={styles.form}>
        <ErrorAlert error={saveMutation.error} />

        <label className="field">
          {t('fields.title')}
          <input
            className="input"
            name="title"
            value={form.title}
            onChange={updateField}
            required
            autoFocus
          />
        </label>

        <label className="field">
          {t('fields.description')}
          <textarea
            className="input"
            name="description"
            rows={3}
            value={form.description}
            onChange={updateField}
          />
        </label>

        <div className="form-grid">
          <label className="field">
            {t('fields.project')}
            <select
              className="input"
              name="projectId"
              value={form.projectId}
              onChange={updateField}
              disabled={isEdit}
              required
            >
              <option value="">{t('tasks.chooseProject')}</option>
              {projects.map((project) => (
                <option key={project.id} value={project.id}>
                  {project.name}
                </option>
              ))}
            </select>
          </label>

          <label className="field">
            {t('fields.assignee')}
            <select
              className="input"
              name="assigneeId"
              value={form.assigneeId}
              onChange={updateField}
            >
              <option value="">{t('tasks.unassigned')}</option>
              {users.map((user) => (
                <option key={user.id} value={user.id}>
                  {fullName(user)}
                </option>
              ))}
            </select>
          </label>

          <label className="field">
            {t('fields.status')}
            <select
              className="input"
              name="taskStatus"
              value={form.taskStatus}
              onChange={updateField}
            >
              {TASK_STATUSES.map((status) => (
                <option key={status} value={status}>
                  {t(`taskStatus.${status}`)}
                </option>
              ))}
            </select>
          </label>

          <label className="field">
            {t('fields.dueDate')}
            <input
              className="input"
              type="date"
              name="dueDate"
              value={form.dueDate}
              onChange={updateField}
              required={isEdit}
            />
          </label>
        </div>

        <fieldset className={styles.fieldset}>
          <legend className={styles.legend}>{t('fields.priority')}</legend>

          <div className={styles.choices}>
            {TASK_PRIORITIES.map((priority) => {
              const isSelected = form.taskPriority === priority;

              return (
                <button
                  key={priority}
                  type="button"
                  aria-pressed={isSelected}
                  className={`${styles.choice} ${isSelected ? styles.choiceSelected : ''}`}
                  style={isSelected ? priorityStyle(priority) : undefined}
                  onClick={() => setForm({ ...form, taskPriority: priority })}
                >
                  {t(`priority.${priority}`)}
                </button>
              );
            })}
          </div>
        </fieldset>

        {labels.length > 0 && (
          <fieldset className={styles.fieldset}>
            <legend className={styles.legend}>{t('fields.labels')}</legend>

            <div className={styles.choices}>
              {labels.map((label) => {
                const isSelected = form.labelIds.includes(label.id);

                return (
                  <button
                    key={label.id}
                    type="button"
                    aria-pressed={isSelected}
                    className={`${styles.choice} ${isSelected ? styles.labelSelected : ''}`}
                    style={isSelected ? { borderColor: label.color } : undefined}
                    onClick={() => toggleLabel(label.id)}
                  >
                    <span className="chip__swatch" style={{ background: label.color }} />
                    {label.name}
                  </button>
                );
              })}
            </div>
          </fieldset>
        )}
      </form>
    </Modal>
  );
}
