export const fetchApplications = () => window
    .fetch("/api/v1/applications", {
        method: 'GET',
        credentials: 'include'
    }).then((response) => response.json())

export const fetchToken = name => window
    .fetch(`/api/v1/applications/${name}/token/on-behalf-of`, {
        method: 'GET',
        credentials: 'include'
    }).then((response) => response.json())