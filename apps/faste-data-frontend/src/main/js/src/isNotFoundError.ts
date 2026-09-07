import { NotFoundError } from '@navikt/dolly-lib';

const isNotFoundError = (error: unknown) =>
  error instanceof NotFoundError ||
  (error instanceof Error && error.name === NotFoundError.name) ||
  (typeof error === 'object' &&
    error !== null &&
    'name' in error &&
    error.name === NotFoundError.name);

export default isNotFoundError;
