import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../../auth/AuthContext';
import { Logo } from '../../components/Logo';
import { LanguageSwitcher } from '../../components/LanguageSwitcher';
import { Icon } from '../../components/Icon';
import { TASK_STATUSES, statusStyle } from '../../lib/taskMeta';
import styles from './LandingPage.module.css';

const GITHUB_URL = 'https://github.com/VitaliyProgrammer/task-management-application';

const FEATURES = [
  { key: 'board', icon: 'dashboard' },
  { key: 'security', icon: 'shield' },
  { key: 'telegram', icon: 'send' },
  { key: 'email', icon: 'mail' },
  { key: 'calendar', icon: 'calendar' },
  { key: 'dropbox', icon: 'paperclip' },
];

const FLOW_STEPS = ['step1', 'step2', 'step3', 'step4'];

const STACK = [
  'Java 19',
  'Spring Boot 3.2',
  'Spring Security + JWT',
  'Hibernate / JPA',
  'MySQL 8',
  'Liquibase',
  'Docker Compose',
  'JUnit 5 + Mockito',
  'Testcontainers',
  'Swagger',
  'React 19',
  'Vite',
  'TanStack Query',
  'i18next',
];

const DIAGRAMS = [
  { file: 'domain-model.png', key: 'domain' },
  { file: 'application-architecture.png', key: 'architecture' },
  { file: 'system-integration.png', key: 'integration' },
];

// Mini board in the hero, made from real UI pieces.
const PREVIEW = {
  NOT_STARTED: ['Design login page', 'Write API README'],
  IN_PROGRESS: ['Kanban board view', 'Telegram alerts'],
  COMPLETED: ['JWT authentication', 'Dropbox files'],
};

export function LandingPage() {
  const { t } = useTranslation();
  const { isAuthenticated } = useAuth();

  const appLink = isAuthenticated ? '/dashboard' : '/login';

  return (
    <div className={styles.page}>
      {/* ===== Top bar ===== */}
      <div className={styles.topBar}>
        <div className={styles.container}>
          <Logo />

          <nav className={styles.topNav} aria-label={t('landing.navigation')}>
            <a href="#features" className={styles.navLink}>
              {t('landing.features')}
            </a>
            <a href="#how" className={styles.navLink}>
              {t('landing.howItWorks')}
            </a>
            <a href="#stack" className={styles.navLink}>
              {t('landing.stack')}
            </a>

            <LanguageSwitcher />

            <Link to={appLink} className="btn btn--ghost btn--small">
              {isAuthenticated ? t('landing.openApp') : t('landing.logIn')}
            </Link>
          </nav>
        </div>
      </div>

      {/* ===== Hero ===== */}
      <section className={`${styles.container} ${styles.hero}`}>
        <div className={styles.heroText}>
          <span className={styles.eyebrow}>{t('landing.eyebrow')}</span>

          <h1 className={styles.heroTitle}>
            {t('landing.heroTitle')} <span>{t('landing.heroAccent')}</span>
          </h1>

          <p className={styles.heroLead}>{t('landing.heroLead')}</p>

          <div className="toolbar">
            <Link
              to={appLink}
              className="btn btn--primary"
              style={{ minHeight: 52, padding: '0 28px', fontSize: 16 }}
            >
              {t('landing.tryDemo')}
            </Link>

            <a href={GITHUB_URL} className="btn btn--ghost" style={{ minHeight: 52, fontSize: 16 }}>
              <Icon name="github" /> {t('landing.viewGithub')}
            </a>
          </div>

          <p className="field__hint" style={{ fontSize: 14 }}>
            {t('landing.demoNote')}
          </p>
        </div>

        <div className={styles.preview} aria-hidden="true">
          {TASK_STATUSES.map((status) => (
            <div key={status} className={styles.previewColumn} style={statusStyle(status)}>
              <span className={styles.previewHeader}>
                <span className="dot" style={{ color: 'var(--pill-dot)' }} />
                {t(`taskStatus.${status}`)}
              </span>

              {PREVIEW[status].map((title) => (
                <span key={title} className={styles.previewCard}>
                  {title}
                  <span className={styles.previewLine} />
                </span>
              ))}
            </div>
          ))}
        </div>
      </section>

      {/* ===== Features ===== */}
      <section id="features" className={`${styles.container} ${styles.section}`}>
        <h2 className={styles.sectionTitle}>{t('landing.featuresTitle')}</h2>
        <p className={styles.sectionLead}>{t('landing.featuresLead')}</p>

        <div className={styles.featureGrid}>
          {FEATURES.map((feature) => (
            <article key={feature.key} className={styles.feature}>
              <span className={styles.featureIcon}>
                <Icon name={feature.icon} size={22} />
              </span>
              <h3 className={styles.featureTitle}>{t(`landing.feature.${feature.key}.title`)}</h3>
              <p className={styles.featureText}>{t(`landing.feature.${feature.key}.text`)}</p>
            </article>
          ))}
        </div>
      </section>

      {/* ===== How notifications work ===== */}
      <section id="how" className={`${styles.container} ${styles.section}`}>
        <h2 className={styles.sectionTitle}>{t('landing.howTitle')}</h2>
        <p className={styles.sectionLead}>{t('landing.howLead')}</p>

        <ol className={styles.flow}>
          {FLOW_STEPS.map((step, index) => (
            <li key={step} className={styles.flowStep}>
              <span className={styles.stepNumber}>{t('landing.step', { number: index + 1 })}</span>
              <span className={styles.stepTitle}>{t(`landing.flow.${step}.title`)}</span>
              <span className={styles.stepText}>{t(`landing.flow.${step}.text`)}</span>
            </li>
          ))}
        </ol>
      </section>

      {/* ===== Stack and diagrams ===== */}
      <section id="stack" className={`${styles.container} ${styles.section}`}>
        <h2 className={styles.sectionTitle}>{t('landing.stackTitle')}</h2>

        <ul className={styles.stack}>
          {STACK.map((item) => (
            <li key={item} className={styles.stackItem}>
              {item}
            </li>
          ))}
        </ul>

        <div className={styles.diagrams}>
          {DIAGRAMS.map((diagram) => (
            <figure key={diagram.file} className={styles.diagram}>
              <a href={`/diagrams/${diagram.file}`} target="_blank" rel="noreferrer">
                <img
                  src={`/diagrams/${diagram.file}`}
                  alt={t(`landing.diagram.${diagram.key}`)}
                  loading="lazy"
                />
              </a>
              <figcaption>{t(`landing.diagram.${diagram.key}`)}</figcaption>
            </figure>
          ))}
        </div>
      </section>

      {/* ===== Footer ===== */}
      <footer className={styles.footer}>
        <div className={styles.container}>
          <span>
            {t('landing.builtBy')} <strong>Vitaliy</strong> · Java Developer
          </span>

          <span className="toolbar" style={{ gap: 20 }}>
            <a href={GITHUB_URL}>GitHub</a>
            <a
              href={`${import.meta.env.VITE_API_URL || 'http://localhost:8080'}/swagger-ui/index.html`}
            >
              Swagger
            </a>
          </span>
        </div>
      </footer>
    </div>
  );
}
