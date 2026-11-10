import { useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { commentsApi } from '../../../api/endpoints';
import { queryKeys, useComments } from '../../../hooks/queries';
import { Avatar } from '../../../components/Badges';
import { ErrorAlert } from '../../../components/States';
import { formatDateTime } from '../../../lib/format';
import { fullName } from '../../../lib/people';
import styles from './details.module.css';

export function CommentsSection({ taskId, usersById }) {
  const { t, i18n } = useTranslation();
  const queryClient = useQueryClient();

  const { comments, isPending } = useComments(taskId);
  const [text, setText] = useState('');

  const addMutation = useMutation({
    mutationFn: () => commentsApi.create(taskId, text.trim()),

    onSuccess: () => {
      setText('');
      queryClient.invalidateQueries({ queryKey: queryKeys.comments(taskId) });
    },
  });

  const handleSubmit = (event) => {
    event.preventDefault();

    if (text.trim()) {
      addMutation.mutate();
    }
  };

  return (
    <section className="card">
      <h2 className="card__title">{t('comments.title')}</h2>

      {!isPending && comments.length === 0 && <p className="field__hint">{t('comments.empty')}</p>}

      {comments.map((comment) => {
        const author = usersById.get(comment.userId);

        return (
          <div key={comment.id} className={styles.comment}>
            <Avatar user={author} size={34} />

            <div className={styles.bubble}>
              <span className={styles.commentMeta}>
                {fullName(author)} <span>· {formatDateTime(comment.timestamp, i18n.language)}</span>
              </span>
              <span className={styles.commentText}>{comment.text}</span>
            </div>
          </div>
        );
      })}

      <form onSubmit={handleSubmit} className={styles.commentForm}>
        <ErrorAlert error={addMutation.error} />

        <label className="field">
          {t('comments.add')}
          <textarea
            className="input"
            rows={2}
            placeholder={t('comments.placeholder')}
            value={text}
            onChange={(event) => setText(event.target.value)}
          />
        </label>

        <button
          type="submit"
          className="btn btn--primary btn--small"
          disabled={!text.trim() || addMutation.isPending}
        >
          {t('comments.post')}
        </button>
      </form>
    </section>
  );
}
