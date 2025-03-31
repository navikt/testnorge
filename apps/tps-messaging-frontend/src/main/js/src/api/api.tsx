import axios from 'axios';
import { NotFoundError } from '@navikt/dolly-lib';

export class ApiError extends Error {
  constructor(
    public status: number,
    message: string,
    public data?: unknown,
  ) {
    super(message);
    this.name = 'ApiError';
  }
}

export class NetworkError extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'NetworkError';
  }
}

// Shared error handler function
const handleAxiosError = (error: unknown, url: string): never => {
  if (axios.isAxiosError(error)) {
    const { response, request } = error;

    if (response) {
      const { status, data } = response;
      switch (status) {
        case 400:
          throw new ApiError(status, `Bad request: ${data?.message || 'Invalid input'}`, data);
        case 401:
          throw new ApiError(status, 'Unauthorized: Authentication required', data);
        case 403:
          throw new ApiError(status, 'Forbidden: Insufficient permissions', data);
        case 404:
          if (data?.error) {
            throw new Error(data.error);
          }
          throw new NotFoundError();
        default:
          if (status >= 500) {
            throw new ApiError(
              status,
              `Server error (${status}): ${data?.message || 'The server failed to process the request'}`,
              data,
            );
          } else {
            throw new ApiError(
              status,
              `Request failed with status ${status}: ${data?.message}`,
              data,
            );
          }
      }
    } else if (request) {
      throw new NetworkError(`Network error: No response received from ${url}`);
    }
  }

  throw new Error(`Unknown error: ${error instanceof Error ? error.message : String(error)}`);
};

export const fetcher = async <TResponse extends any = unknown>(
  url: string,
  headers: Record<string, string> = {},
): Promise<TResponse> => {
  try {
    const response = await axios.get<TResponse>(url, { headers });
    return response.data;
  } catch (error) {
    return handleAxiosError(error, url);
  }
};

export const postData = async <TResponse = unknown, TRequest = unknown>(
  url: string,
  body: TRequest,
  headers: Record<string, string> = {},
): Promise<TResponse> => {
  try {
    const response = await axios.post<TResponse>(url, body, { headers });
    return response.data;
  } catch (error) {
    return handleAxiosError(error, url);
  }
};
