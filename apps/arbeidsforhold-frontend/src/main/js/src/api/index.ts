type Fetch = {
  url: string;
  method: "GET" | "POST" | "PUT" | "DELETE";
  body?: unknown;
  headers?: string[][];
};

const _fetch = ({ url, method, body, headers }: Fetch): Promise<Response> =>
  window
    .fetch(url, {
      method: method,
      credentials: "include",
      headers: headers,
      body: !!body ? JSON.stringify(body) : null,
    })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Response fra endepunkt var ikke ok");
      }
      return response;
    })
    .catch((error) => {
      console.error(error);
      throw error;
    });

export default {
  fetch: _fetch,
};
