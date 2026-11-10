// Everything the UI needs to know about statuses and priorities, in one place.
// Colors come from CSS variables in styles/tokens.css.

export const TASK_STATUSES = ['NOT_STARTED', 'IN_PROGRESS', 'COMPLETED'];

export const TASK_PRIORITIES = ['HIGH', 'MEDIUM', 'LOW'];

export const PROJECT_STATUSES = ['INITIATED', 'IN_PROGRESS', 'COMPLETED'];

const STATUS_STYLE = {
  NOT_STARTED: 'not-started',
  INITIATED: 'not-started',
  IN_PROGRESS: 'in-progress',
  COMPLETED: 'completed',
  NO_SPECIFIED: 'unspecified',
};

// Inline CSS variables for .status-pill / board columns
export function statusStyle(status) {
  const key = STATUS_STYLE[status] ?? STATUS_STYLE.NO_SPECIFIED;

  return {
    '--pill-bg': `var(--status-${key}-bg)`,
    '--pill-dot': `var(--status-${key}-dot)`,
    '--pill-ink': `var(--status-${key}-ink)`,
  };
}

// Inline CSS variables for .badge
export function priorityStyle(priority) {
  const key = (priority ?? 'LOW').toLowerCase();

  return {
    '--badge-bg': `var(--priority-${key}-bg)`,
    '--badge-ink': `var(--priority-${key}-ink)`,
    '--badge-border': `var(--priority-${key}-border)`,
    '--badge-shadow': `var(--priority-${key}-shadow)`,
  };
}

// Tasks created without a status are shown in the first column.
export function boardColumnOf(task) {
  return TASK_STATUSES.includes(task.taskStatus) ? task.taskStatus : 'NOT_STARTED';
}
