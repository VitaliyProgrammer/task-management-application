import styles from './Logo.module.css';

// The approved logo: a 3D lightning arrow + "Alijenta" with a yellow "j".
// Sizes come from --logo-mark-width / --logo-text-size in tokens.css.
export function LogoMark({ className }) {
  return (
    <svg className={className} viewBox="0 0 50 35" aria-hidden="true">
      <polygon
        points="4.5,29.5 17.5,13.5 24.5,24.5 35.5,12.5 32.5,9.5 48.5,6.5 45.5,22.5 41.5,17.5 25.5,32.5 18.5,21.5"
        fill="var(--color-primary-deep)"
        stroke="var(--color-primary-deep)"
        strokeWidth="1.5"
        strokeLinejoin="round"
      />

      <polygon
        points="2,27 15,11 22,22 33,10 30,7 46,4 43,20 39,15 23,30 16,19"
        fill="var(--color-primary)"
        stroke="var(--color-primary)"
        strokeWidth="1.5"
        strokeLinejoin="round"
      />

      <polyline
        points="4,25 15,12 21.5,22.4 32.6,10.6"
        fill="none"
        stroke="var(--color-primary-highlight)"
        strokeWidth="1.4"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  );
}

export function Logo({ inverted = false }) {
  return (
    <span className={`${styles.logo} ${inverted ? styles.inverted : ''}`}>
      <LogoMark className={styles.mark} />

      <span className={styles.wordmark}>
        Ali<span className={styles.j}>j</span>enta
      </span>
    </span>
  );
}
