import { useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { projectsApi } from '../../api/endpoints';
import { queryKeys } from '../../hooks/queries';
import { Modal } from '../../components/Modal';
import { ErrorAlert } from '../../components/States';
import { PROJECT_STATUSES } from '../../lib/taskMeta';
import { toInputDate } from '../../lib/format';

export function ProjectFormModal({ project, onClose }) {
  const { t } = useTranslation();
  const queryClient = useQueryClient();

  const isEdit = Boolean(project);

  const [form, setForm] = useState({
    name: project?.name ?? '',
    description: project?.description ?? '',
    startDate: project?.startDate ?? toInputDate(new Date()),
    endDate: project?.endDate ?? '',
    status: project?.status ?? 'INITIATED',
  });

  const saveMutation = useMutation({
    mutationFn: () => {
      const request = {
        name: form.name.trim(),
        description: form.description.trim(),
        startDate: form.startDate,
        endDate: form.endDate || null,
      };

      // A new project always starts as INITIATED on the backend.
      return isEdit
        ? projectsApi.update(project.id, { ...request, status: form.status })
        : projectsApi.create(request);
    },

    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.projects });
      onClose();
    },
  });

  const updateField = (event) => {
    setForm({ ...form, [event.target.name]: event.target.value });
  };

  return (
    <Modal
      title={isEdit ? t('projects.editProject') : t('projects.newProject')}
      onClose={onClose}
      width={560}
      footer={
        <>
          <button type="button" className="btn btn--ghost" onClick={onClose}>
            {t('common.cancel')}
          </button>

          <button
            type="submit"
            form="project-form"
            className="btn btn--primary"
            disabled={saveMutation.isPending}
          >
            {t('common.save')}
          </button>
        </>
      }
    >
      <form
        id="project-form"
        style={{ display: 'grid', gap: 16 }}
        onSubmit={(event) => {
          event.preventDefault();
          saveMutation.mutate();
        }}
      >
        <ErrorAlert error={saveMutation.error} />

        <label className="field">
          {t('fields.name')}
          <input
            className="input"
            name="name"
            value={form.name}
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
            required
          />
        </label>

        <div className="form-grid">
          <label className="field">
            {t('fields.startDate')}
            <input
              className="input"
              type="date"
              name="startDate"
              value={form.startDate}
              onChange={updateField}
              required
            />
          </label>

          <label className="field">
            {t('fields.endDate')}
            <input
              className="input"
              type="date"
              name="endDate"
              value={form.endDate}
              onChange={updateField}
            />
          </label>
        </div>

        {isEdit && (
          <label className="field">
            {t('fields.status')}
            <select className="input" name="status" value={form.status} onChange={updateField}>
              {PROJECT_STATUSES.map((status) => (
                <option key={status} value={status}>
                  {t(`projectStatus.${status}`)}
                </option>
              ))}
            </select>
          </label>
        )}
      </form>
    </Modal>
  );
}
