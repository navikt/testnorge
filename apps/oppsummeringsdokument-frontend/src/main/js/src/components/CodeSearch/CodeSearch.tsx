import React, { useState } from "react";
import { Input, Label, SkjemaGruppe } from "nav-frontend-skjema";
import { Datepicker } from "nav-datovelger";

import "./CodeSearch.less";
import { Api } from "@navikt/dolly-lib";
import { FetchCode } from "@/components";
import { Hovedknapp, Knapp } from "nav-frontend-knapper";
// @ts-ignore
import { CopyToClipboard } from "react-copy-to-clipboard/lib/Component";

type FetchFromPosition = (position: number) => Promise<Response>;

const createQueryParam = (value: string[][]) => {
  if (value.reduce((count, item) => count + item.length, 0) === 0) {
    return "";
  }
  return "?" + value.map((value) => value[0] + "=" + value[1]).join("&");
};

export const CodeSearch = () => {
  const [env, setEnv] = useState<string>("t4");
  const [fom, setFom] = useState<string>("");
  const [tom, setTom] = useState<string>("");
  const [ident, setIdent] = useState<string>("");
  const [typeArbeidsforhold, setTypeArbeidsforhold] = useState<string>("");
  const [id, setId] = useState<string>(null);

  const search = (): FetchFromPosition => {
    const param: string[][] = [];
    if (fom && fom !== "") {
      param.push(["fom", fom]);
    }
    if (tom && tom !== "") {
      param.push(["tom", tom]);
    }
    if (ident && ident !== "") {
      param.push(["ident", ident]);
    }

    if (typeArbeidsforhold && typeArbeidsforhold !== "") {
      param.push(["typeArbeidsforhold", typeArbeidsforhold]);
    }

    return (page) => {
      return Api.fetch(
        "/api/v1/oppsummeringsdokumenter/raw/items" +
          createQueryParam(param.concat([["page", page.toString()]])),
        {
          method: "GET",
          headers: {
            miljo: env,
          },
        }
      ).then((response) => {
        setId(response.headers.get("Element-Id"));
        return response;
      });
    };
  };

  const [fetchFromPosition, setFetchFromPosition] = useState<FetchFromPosition>(
    search
  );

  return (
    <div className="code-search">
      <div className="code-search__schema">
        <SkjemaGruppe className="code-search--border" legend="Filtrering">
          <Input
            defaultValue="t4"
            label="Miljø"
            onBlur={(e) => {
              if (e.target.value === "") {
                setEnv(null);
              } else {
                setEnv(e.target.value);
              }
            }}
          />
          <Input
            defaultValue=""
            label="Ident"
            onBlur={(e) => {
              if (e.target.value === "") {
                setIdent(null);
              } else {
                setIdent(e.target.value);
              }
            }}
          />
          <Input
            defaultValue=""
            label="Type Arbeidsforhold"
            onBlur={(e) => {
              if (e.target.value === "") {
                setTypeArbeidsforhold(null);
              } else {
                setTypeArbeidsforhold(e.target.value);
              }
            }}
          />
          <Label htmlFor="datepicker-fom-input">Fom</Label>
          <div className="code-search__datepicker">
            <Datepicker
              locale={"nb"}
              inputId="datepicker-fom-input"
              value={fom}
              onChange={setFom}
              calendarSettings={{ showWeekNumbers: true }}
              showYearSelector={true}
            />
          </div>
          <Label htmlFor="datepicker-tom-input">Tom</Label>
          <div className="code-search__datepicker">
            <Datepicker
              locale={"nb"}
              inputId="datepicker-tom-input"
              value={tom}
              onChange={setTom}
              calendarSettings={{ showWeekNumbers: true }}
              showYearSelector={true}
            />
          </div>
          <Hovedknapp onClick={() => setFetchFromPosition(search)}>
            Filter
          </Hovedknapp>
          {id && (
            <CopyToClipboard
              text={
                window.location.href +
                "api/v1/oppsummeringsdokumenter/raw/items/" +
                id
              }
            >
              <Knapp>Kopier lenke</Knapp>
            </CopyToClipboard>
          )}
        </SkjemaGruppe>
      </div>
      <FetchCode fetchFromPosition={fetchFromPosition} />
    </div>
  );
};
