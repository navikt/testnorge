import { Api } from "@navikt/dolly-lib";

const fetchApplications = () =>
  Api.fetchJson<string[]>("/api/v1/applications", { method: "GET" });

const fetchToken = (name: string) =>
  Api.fetchJson<{ token: string }>(
    `/api/v1/applications/${name}/token/on-behalf-of`,
    {
      method: "GET",
    }
  );

export default { fetchApplications, fetchToken };
