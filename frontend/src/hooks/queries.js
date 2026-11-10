import { useMemo } from 'react';
import { useQuery } from '@tanstack/react-query';
import {
  attachmentsApi,
  commentsApi,
  labelsApi,
  projectsApi,
  tasksApi,
  usersApi,
} from '../api/endpoints';

// Query keys in one place. Mutations use them to say "this data is outdated now".
export const queryKeys = {
  profile: ['profile'],
  tasks: ['tasks'],
  task: (id) => ['tasks', 'one', String(id)],
  projects: ['projects'],
  labels: ['labels'],
  users: ['users'],
  comments: (taskId) => ['comments', String(taskId)],
  attachments: (taskId) => ['attachments', String(taskId)],
};

// Spring returns pages: { content: [...], totalElements, totalPages, number, size }
const contentOf = (page) => page?.content ?? [];

export function useTasks(params = {}) {
  return useQuery({
    queryKey: [...queryKeys.tasks, params],
    queryFn: () => tasksApi.search({ size: 500, ...params }),
  });
}

export function useTask(id) {
  return useQuery({
    queryKey: queryKeys.task(id),
    queryFn: () => tasksApi.get(id),
  });
}

export function useProjects() {
  const query = useQuery({ queryKey: queryKeys.projects, queryFn: projectsApi.list });

  return { ...query, projects: contentOf(query.data) };
}

export function useLabels() {
  const query = useQuery({ queryKey: queryKeys.labels, queryFn: labelsApi.list });

  return { ...query, labels: contentOf(query.data) };
}

// Users plus a lookup map: usersById.get(task.assigneeId)
export function useUsers() {
  const query = useQuery({ queryKey: queryKeys.users, queryFn: usersApi.list });

  const users = contentOf(query.data);

  const usersById = useMemo(() => new Map(users.map((user) => [user.id, user])), [users]);

  return { ...query, users, usersById };
}

export function useProjectsById() {
  const { projects } = useProjects();

  return useMemo(() => new Map(projects.map((project) => [project.id, project])), [projects]);
}

export function useComments(taskId) {
  const query = useQuery({
    queryKey: queryKeys.comments(taskId),
    queryFn: () => commentsApi.list(taskId),
  });

  return { ...query, comments: contentOf(query.data) };
}

export function useAttachments(taskId) {
  const query = useQuery({
    queryKey: queryKeys.attachments(taskId),
    queryFn: () => attachmentsApi.list(taskId),
  });

  return { ...query, attachments: contentOf(query.data) };
}
