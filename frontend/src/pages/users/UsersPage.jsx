import { useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../../auth/AuthContext';
import { usersApi } from '../../api/endpoints';
import { queryKeys, useUsers } from '../../hooks/queries';
import { Avatar } from '../../components/Badges';
import { ErrorAlert, ErrorState, LoadingState } from '../../components/States';
import { Icon } from '../../components/Icon';
import { fullName } from '../../lib/people';
import styles from './UsersPage.module.css';

const ROLES = ['USER', 'ADMIN'];

export function UsersPage() {
  const { t } = useTranslation();
  const { user: currentUser } = useAuth();
  const queryClient = useQueryClient();

  const usersQuery = useUsers();
  const [search, setSearch] = useState('');

  const roleMutation = useMutation({
    mutationFn: ({ id, role }) => usersApi.changeRole(id, role),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: queryKeys.users }),
  });

  const query = search.trim().toLowerCase();

  const visibleUsers = usersQuery.users.filter((user) =>
    `${fullName(user)} ${user.email}`.toLowerCase().includes(query),
  );

  return (
    <main className="page">
      <header className="page-header">
        <div>
          <h1 className="page-title">{t('nav.users')}</h1>
          <p className="page-subtitle">{t('users.subtitle')}</p>
        </div>

        <input
          className="input"
          type="search"
          placeholder={t('users.searchPlaceholder')}
          aria-label={t('users.searchPlaceholder')}
          value={search}
          onChange={(event) => setSearch(event.target.value)}
          style={{ width: 280, maxWidth: '100%', borderRadius: 'var(--radius-pill)' }}
        />
      </header>

      <ErrorAlert error={roleMutation.error} />

      {usersQuery.isPending && <LoadingState />}
      {usersQuery.isError && <ErrorState error={usersQuery.error} />}

      {usersQuery.isSuccess && (
        <div className="table-box">
          <table className={`table ${styles.table}`}>
            <thead>
              <tr>
                <th className={styles.userColumn}>
                  <span className={styles.userHeader}>{t('users.user')}</span>
                </th>
                <th className={styles.center}>{t('users.notifications')}</th>
                <th className={styles.roleColumn}>
                  <span className={styles.roleHeader}>{t('users.role')}</span>
                </th>
              </tr>
            </thead>

            <tbody>
              {visibleUsers.map((user) => {
                const role = user.roles.includes('ADMIN') ? 'ADMIN' : 'USER';
                const isMe = user.id === currentUser.id;

                return (
                  <tr key={user.id}>
                    <td className={styles.userColumn}>
                      <span className={styles.person}>
                        <Avatar user={user} size={38} />
                        <span className={styles.personText}>
                          <strong>{fullName(user)}</strong>
                          <span className="field__hint">{user.email}</span>
                        </span>
                      </span>
                    </td>

                    <td className={styles.center}>
                      <span className={styles.flags}>
                        <ConnectionFlag isOn={user.telegramConnected} label="Telegram" />
                        <ConnectionFlag isOn={user.emailConnected} label="Email" />
                      </span>
                    </td>

                    <td className={styles.roleColumn}>
                      <span className={styles.selectBox}>
                        <select
                          className={styles.select}
                          aria-label={t('users.roleOf', { name: fullName(user) })}
                          value={role}
                          disabled={isMe || roleMutation.isPending}
                          title={isMe ? t('users.cannotChangeOwnRole') : undefined}
                          onChange={(event) =>
                            roleMutation.mutate({ id: user.id, role: event.target.value })
                          }
                        >
                          {ROLES.map((item) => (
                            <option key={item} value={item}>
                              {item}
                            </option>
                          ))}
                        </select>
                        <Icon
                          name="chevronDown"
                          size={14}
                          strokeWidth={2.5}
                          className={styles.chevron}
                        />
                      </span>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}
    </main>
  );
}

function ConnectionFlag({ isOn, label }) {
  return <span className={`${styles.flag} ${isOn ? styles.flagOn : ''}`}>{label}</span>;
}
