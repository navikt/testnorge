import { Api } from "@navikt/dolly-lib";

const fetchToken = (scope: string) =>
  Api.fetchJson<{ token: string }>(
    `/api/v1/tokens/${scope}/token/on-behalf-of`,
    {
      method: "GET",
    }
  );

export default { fetchToken };
