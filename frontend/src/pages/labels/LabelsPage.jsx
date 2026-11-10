import { useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../../auth/AuthContext';
import { labelsApi } from '../../api/endpoints';
import { queryKeys, useLabels } from '../../hooks/queries';
import { LabelChip } from '../../components/Badges';
import { ConfirmDialog } from '../../components/Modal';
import { EmptyState, ErrorAlert, ErrorState, LoadingState } from '../../components/States';
import { Icon } from '../../components/Icon';
import styles from './LabelsPage.module.css';

const PALETTE = [
  '#E57373',
  '#F59E0B',
  '#24955A',
  '#0EA5E9',
  '#8B5CF6',
  '#DB2777',
  '#64748B',
  '#123524',
];

const EMPTY_FORM = { name: '', color: PALETTE[2], description: '' };

export function LabelsPage() {
  const { t } = useTranslation();
  const { isAdmin } = useAuth();
  const queryClient = useQueryClient();

  const labelsQuery = useLabels();

  const [form, setForm] = useState(EMPTY_FORM);
  const [editingId, setEditingId] = useState(null);
  const [deleting, setDeleting] = useState(null);

  const refreshLabels = () => queryClient.invalidateQueries({ queryKey: queryKeys.labels });

  const resetForm = () => {
    setForm(EMPTY_FORM);
    setEditingId(null);
  };

  const saveMutation = useMutation({
    mutationFn: () => (editingId ? labelsApi.update(editingId, form) : labelsApi.create(form)),

    onSuccess: () => {
      refreshLabels();
      resetForm();
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (label) => labelsApi.remove(label.id),

    onSuccess: () => {
      refreshLabels();
      setDeleting(null);
    },
  });

  const startEditing = (label) => {
    setEditingId(label.id);
    setForm({ name: label.name, color: label.color, description: label.description ?? '' });
  };

  const updateField = (event) => {
    setForm({ ...form, [event.target.name]: event.target.value });
  };

  const { labels } = labelsQuery;

  return (
    <main className="page">
      <header>
        <h1 className="page-title">{t('nav.labels')}</h1>
        <p className="page-subtitle">
          {isAdmin ? t('labels.adminHint') : t('labels.readOnlyHint')}
        </p>
      </header>

      <ErrorAlert error={deleteMutation.error} />

      <div className={styles.layout}>
        {/* ===== List ===== */}
        <section className={`card ${styles.list}`}>
          {labelsQuery.isPending && <LoadingState />}
          {labelsQuery.isError && <ErrorState error={labelsQuery.error} />}
          {labelsQuery.isSuccess && labels.length === 0 && (
            <EmptyState>{t('labels.empty')}</EmptyState>
          )}

          {labels.map((label) => (
            <div key={label.id} className={styles.row}>
              <span className={styles.chipCell}>
                <LabelChip label={label} />
              </span>

              <span className={styles.description}>{label.description}</span>
              <code className={styles.hex}>{label.color}</code>

              {isAdmin && (
                <span className="toolbar" style={{ gap: 6 }}>
                  <button
                    type="button"
                    className="icon-btn"
                    aria-label={`${t('common.edit')} ${label.name}`}
                    onClick={() => startEditing(label)}
                  >
                    <Icon name="edit" size={16} />
                  </button>

                  <button
                    type="button"
                    className="icon-btn icon-btn--danger"
                    aria-label={`${t('common.delete')} ${label.name}`}
                    onClick={() => setDeleting(label)}
                  >
                    <Icon name="trash" size={16} />
                  </button>
                </span>
              )}
            </div>
          ))}
        </section>

        {/* ===== Form (administrators only) ===== */}
        {isAdmin && (
          <form
            className={`card ${styles.form}`}
            onSubmit={(event) => {
              event.preventDefault();
              saveMutation.mutate();
            }}
          >
            <h2 className={styles.formTitle}>
              {editingId ? t('labels.editLabel') : t('labels.newLabel')}
            </h2>

            <ErrorAlert error={saveMutation.error} />

            <label className="field">
              {t('fields.name')}
              <input
                className="input"
                name="name"
                value={form.name}
                onChange={updateField}
                required
              />
            </label>

            <fieldset className={styles.fieldset}>
              <legend className={styles.legend}>{t('fields.color')}</legend>

              <div className={styles.palette}>
                {PALETTE.map((color) => (
                  <button
                    key={color}
                    type="button"
                    className={`${styles.swatch} ${form.color === color ? styles.swatchSelected : ''}`}
                    style={{ background: color }}
                    aria-label={color}
                    aria-pressed={form.color === color}
                    onClick={() => setForm({ ...form, color })}
                  />
                ))}

                <input
                  type="color"
                  className={styles.colorInput}
                  aria-label={t('labels.customColor')}
                  value={form.color}
                  onChange={(event) =>
                    setForm({ ...form, color: event.target.value.toUpperCase() })
                  }
                />
              </div>
            </fieldset>

            <label className="field">
              <span>
                {t('fields.description')}{' '}
                <span className="field__hint">({t('labels.optional')})</span>
              </span>
              <textarea
                className="input"
                name="description"
                rows={2}
                maxLength={100}
                value={form.description}
                onChange={updateField}
              />
            </label>

            <div className={styles.preview}>
              {t('labels.preview')}{' '}
              <LabelChip label={{ name: form.name || '…', color: form.color }} />
            </div>

            <div className="toolbar" style={{ justifyContent: 'flex-end' }}>
              {editingId && (
                <button type="button" className="btn btn--ghost btn--small" onClick={resetForm}>
                  {t('common.cancel')}
                </button>
              )}

              <button
                type="submit"
                className="btn btn--primary btn--small"
                disabled={saveMutation.isPending}
              >
                {editingId ? t('common.save') : t('labels.create')}
              </button>
            </div>
          </form>
        )}
      </div>

      {deleting && (
        <ConfirmDialog
          title={t('labels.deleteTitle')}
          message={t('labels.deleteMessage', { name: deleting.name })}
          isPending={deleteMutation.isPending}
          onConfirm={() => deleteMutation.mutate(deleting)}
          onCancel={() => setDeleting(null)}
        />
      )}
    </main>
  );
}
