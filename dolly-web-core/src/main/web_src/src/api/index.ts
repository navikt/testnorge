type Method = "POST" | "GET" | "PUT" | "DELETE";

const _fetch = (url: string, method: Method, body?: object): Promise<Response> => (
    window.fetch(url, {
        method: method,
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(body)
    }).then((response: Response) => {
        if (!response.ok) {
            throw new Error("Response fra endepunkt var ikke ok")
        }
        return response
    }).catch((error: Error) => {
            console.error(error);
            throw error;
    })
);

const fetchJson = <T>(url: string, method: Method, body?: object): Promise<T> =>(
    _fetch(url, method, body).then((response: Response) => response.json() as Promise<T>)
);

export default {
    fetch: _fetch,
    fetchJson
}