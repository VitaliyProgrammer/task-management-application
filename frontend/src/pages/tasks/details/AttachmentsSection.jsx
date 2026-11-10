import { useRef } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { attachmentsApi } from '../../../api/endpoints';
import { queryKeys, useAttachments } from '../../../hooks/queries';
import { DropboxIcon, Icon } from '../../../components/Icon';
import { ErrorAlert } from '../../../components/States';
import { formatDate } from '../../../lib/format';
import styles from './details.module.css';

// "report.pdf" -> "PDF"
const extensionOf = (filename) => filename.split('.').pop().slice(0, 4).toUpperCase();

// The browser saves a Blob as a file through a temporary link.
function saveFile(blob, filename) {
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');

  link.href = url;
  link.download = filename;
  link.click();

  URL.revokeObjectURL(url);
}

export function AttachmentsSection({ taskId }) {
  const { t, i18n } = useTranslation();
  const queryClient = useQueryClient();
  const fileInput = useRef(null);

  const { attachments, isPending } = useAttachments(taskId);

  const uploadMutation = useMutation({
    mutationFn: (file) => attachmentsApi.upload(taskId, file),

    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeys.attachments(taskId) }),

    onSettled: () => {
      fileInput.current.value = '';
    },
  });

  const downloadMutation = useMutation({
    mutationFn: (attachment) => attachmentsApi.download(attachment.id),

    onSuccess: (blob, attachment) => saveFile(blob, attachment.filename),
  });

  const handleFileChange = (event) => {
    const [file] = event.target.files;

    if (file) {
      uploadMutation.mutate(file);
    }
  };

  return (
    <section className="card">
      <div className={styles.sectionHeader}>
        <h2 className="card__title">{t('attachments.title')}</h2>

        <span className={styles.storedIn}>
          <DropboxIcon size={16} /> {t('attachments.storedInDropbox')}
        </span>
      </div>

      <ErrorAlert error={uploadMutation.error ?? downloadMutation.error} />

      {!isPending && attachments.length === 0 && (
        <p className="field__hint">{t('attachments.empty')}</p>
      )}

      {attachments.map((attachment) => (
        <div key={attachment.id} className={styles.fileRow}>
          <span className={styles.fileIcon}>{extensionOf(attachment.filename)}</span>

          <span className={styles.fileText}>
            <span className={styles.fileName}>{attachment.filename}</span>
            <span className="field__hint">
              {t('attachments.uploaded')} {formatDate(attachment.uploadDate, i18n.language)}
            </span>
          </span>

          <button
            type="button"
            className="icon-btn"
            aria-label={`${t('attachments.download')} ${attachment.filename}`}
            onClick={() => downloadMutation.mutate(attachment)}
          >
            <Icon name="download" />
          </button>
        </div>
      ))}

      <label className={styles.dropzone}>
        <Icon name="upload" />
        {uploadMutation.isPending ? t('attachments.uploading') : t('attachments.upload')}
        <input
          ref={fileInput}
          type="file"
          className="visually-hidden"
          onChange={handleFileChange}
        />
      </label>
    </section>
  );
}
