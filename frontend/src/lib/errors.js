// Turns any failed request into text a person can read.
//
// The backend answers with RFC 9457 "problem details":
//   { "title": "Not Found", "status": 404, "detail": "Task not found!" }
// and for validation:
//   { "title": "Bad Request", "detail": "Request validation failed", "errors": ["..."] }

export function getErrorMessages(error, t) {
  const problem = error?.response?.data;

  if (!error?.response) {
    return [t('errors.network')];
  }

  if (Array.isArray(problem?.errors) && problem.errors.length > 0) {
    return problem.errors;
  }

  if (problem?.detail) {
    return [problem.detail];
  }

  if (error.response.status === 403) {
    return [t('errors.forbidden')];
  }

  return [t('errors.unknown')];
}
