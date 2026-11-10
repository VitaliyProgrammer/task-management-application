import { Link } from 'react-router-dom';
import { Logo } from '../../components/Logo';
import { LanguageSwitcher } from '../../components/LanguageSwitcher';
import styles from './AuthLayout.module.css';

// Shared frame for login and registration: green brand panel + form.
export function AuthLayout({ title, text, aside, children }) {
  return (
    <div className={styles.page}>
      <section className={styles.brandPanel}>
        <Link to="/" className={styles.brandLink}>
          <span className={styles.logoTile}>
            <Logo />
          </span>
        </Link>

        <div className={styles.brandText}>
          <h1 className={styles.brandTitle}>{title}</h1>
          <p className={styles.brandSubtitle}>{text}</p>
        </div>

        <div className={styles.brandAside}>{aside}</div>
      </section>

      <main className={styles.formSide}>
        <div className={styles.formBox}>
          <LanguageSwitcher className={styles.language} />
          {children}
        </div>
      </main>
    </div>
  );
}
