import React, { useState } from "react";
import { Input, Label, SkjemaGruppe } from "nav-frontend-skjema";
import { Datepicker } from "nav-datovelger";

import "./CodeSearch.less";
import Api from "@/api";
import { FetchCode } from "@/componetes";
import { Hovedknapp } from "nav-frontend-knapper";

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

  const search = (): FetchFromPosition => {
    const param: string[][] = [];
    if (fom && fom !== "") {
      param.push(["fom", fom]);
    }
    if (tom && tom !== "") {
      param.push(["tom", tom]);
    }

    return (item) =>
      Api.fetch({
        url:
          "/api/v1/oppsummeringsdokumenter/raw/items/" +
          item +
          createQueryParam(param),
        method: "GET",
        headers: [["miljo", env]],
      });
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
        </SkjemaGruppe>
      </div>
      <FetchCode fetchFromPosition={fetchFromPosition} />
    </div>
  );
};
