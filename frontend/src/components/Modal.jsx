import { useEffect } from 'react';
import { createPortal } from 'react-dom';
import { useTranslation } from 'react-i18next';
import { Icon } from './Icon';
import styles from './Modal.module.css';

// A dialog rendered on top of the page.
// createPortal puts it at the end of <body>, outside of any parent overflow/z-index.
export function Modal({ title, onClose, children, footer, width = 640 }) {
  const { t } = useTranslation();

  // Close with the Escape key
  useEffect(() => {
    const onKeyDown = (event) => {
      if (event.key === 'Escape') {
        onClose();
      }
    };

    document.addEventListener('keydown', onKeyDown);

    return () => document.removeEventListener('keydown', onKeyDown);
  }, [onClose]);

  return createPortal(
    <div className={styles.backdrop} onMouseDown={onClose}>
      <div
        className={styles.dialog}
        style={{ maxWidth: width }}
        role="dialog"
        aria-modal="true"
        aria-label={title}
        onMouseDown={(event) => event.stopPropagation()}
      >
        <div className={styles.header}>
          <h2 className={styles.title}>{title}</h2>

          <button type="button" className="icon-btn" aria-label={t('common.close')} onClick={onClose}>
            <Icon name="close" size={20} />
          </button>
        </div>

        <div className={styles.body}>{children}</div>

        {footer && <div className={styles.footer}>{footer}</div>}
      </div>
    </div>,
    document.body,
  );
}

// "Are you sure?" built on top of Modal.
export function ConfirmDialog({ title, message, confirmLabel, onConfirm, onCancel, isPending }) {
  const { t } = useTranslation();

  return (
    <Modal
      title={title}
      onClose={onCancel}
      width={440}
      footer={
        <>
          <button type="button" className="btn btn--ghost" onClick={onCancel}>
            {t('common.cancel')}
          </button>

          <button type="button" className="btn btn--danger" onClick={onConfirm} disabled={isPending}>
            {confirmLabel ?? t('common.delete')}
          </button>
        </>
      }
    >
      <p>{message}</p>
    </Modal>
  );
}
