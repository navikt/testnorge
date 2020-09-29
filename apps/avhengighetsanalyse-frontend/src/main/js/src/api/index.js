const _fetch = (url, method, body) => (
    window.fetch(url, {
        method: method,
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json'
        },
        body: !!body ? JSON.stringify(body) : null
    }).then(response => {
        if (!response.ok) {
            throw new Error("Response fra endepunkt var ikke ok")
        }
        return response
    }).then(response => response.json()
    ).catch((error) => {
        console.error(error);
        throw error;
    })
);

export const fetchDependencies = () => _fetch("/api/v1/dependencies", "GET")
    .then(data => {
        const application = new Map();
        data.forEach(app => {
            application.set(app.applicationName, {
                id: app.applicationName,
                name: app.applicationName,
            });
            app.dependencies.forEach(dependency => application.set(dependency.name,{
                id: dependency.name,
                color: dependency.external ? "#FF7F50" : "lightgreen"
            }));
        });

        const links = data
            .filter(app => app.dependencies.length > 0)
            .reduce((result, app) => {
                app.dependencies.forEach(dependency => {
                    result.push({
                        source: app.applicationName,
                        target: dependency.name
                    })
                })
                return result;
            }, []);

        return Promise.resolve({
            nodes: Array.from(application.values()),
            links
        })
    });