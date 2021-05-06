import { Form, Line, SelectFormItem, Pageable, Knapp } from '@navikt/dolly-komponenter';
import React, { useEffect, useState } from 'react';
import { OrganisasjonFasteDataService, OrganisasjonService } from '@/service';
import { Organisasjon as FasteDataOrganisasjon } from '@/service/OrganisasjonFasteDataService';
import { CompareTable } from '@/components/compare-table';
import { OrganisasjonComperator } from '@/comperator';
import { Input } from 'nav-frontend-skjema';

type Gruppe =
  | 'DOLLY'
  | 'SAMHANDLER'
  | 'ANDRE'
  | 'OFFENTLIGE'
  | 'EKSTERNE'
  | 'OTP'
  | 'INTERNE'
  | 'HJELPEGRUPPE'
  | 'WIP';

const grupper = [
  'DOLLY',
  'SAMHANDLER',
  'ANDRE',
  'OFFENTLIGE',
  'EKSTERNE',
  'OTP',
  'INTERNE',
  'HJELPEGRUPPE',
  'WIP',
];

const miljoer = [
  'q1',
  'q2',
  'q4',
  'q5',
  'qx',
  't0',
  't1',
  't13',
  't2',
  't3',
  't4',
  't5',
  't6',
  'u5',
];

const toOptions = (options: string[]) =>
  options.map((value) => ({
    value: value,
    label: value.toUpperCase(),
  }));

export default () => {
  const [organisasjoner, setOrganisasjoner] = useState<FasteDataOrganisasjon[]>();
  const [gruppe, setGruppe] = useState<Gruppe>('DOLLY');
  const [miljo, setMiljo] = useState<string>('q1');
  const [loading, setLoading] = useState<boolean>(false);
  const [tag, setTag] = useState<string>('');
  const [opprinnelse, setOpprinnelse] = useState<string>('');

  const onSearch = (gruppe: Gruppe) => {
    setLoading(true);
    OrganisasjonFasteDataService.fetchOrganisasjoner(
      gruppe,
      !tag ? null : tag,
      !opprinnelse ? null : opprinnelse
    )
      .then((response) => {
        setOrganisasjoner(response);
        setLoading(false);
      })
      .catch(() => setLoading(false));
  };

  useEffect(() => {
    setOrganisasjoner(null);
  }, [gruppe, miljo, tag, opprinnelse]);

  return (
    <Form>
      <Line>
        <SelectFormItem
          label="Gruppe"
          htmlId="gruppe-selec"
          onChange={(value: Gruppe) => setGruppe(value)}
          options={toOptions(grupper)}
        />
        <SelectFormItem
          label="Miljø"
          htmlId="miljo-select"
          onChange={(value: string) => setMiljo(value)}
          options={toOptions(miljoer)}
        />
        <Input
          label="Tag"
          value={tag}
          onChange={(e) => {
            setTag(e.target.value);
          }}
        />
      </Line>
      <Line>
        <Input
          label="Opprinnelse"
          value={opprinnelse}
          onChange={(e) => {
            setOpprinnelse(e.target.value);
          }}
        />
        <Knapp
          disabled={loading}
          onClick={(e) => {
            e.preventDefault();
            onSearch(gruppe);
          }}
        >
          Søk
        </Knapp>
      </Line>
      {organisasjoner && (
        <Pageable
          items={organisasjoner.map((organisasjon) => ({
            ...organisasjon,
            id: organisasjon.orgnummer,
          }))}
          render={(items, index) => (
            <CompareTable
              key={index}
              miljo={miljo}
              linkPath="organisasjon"
              fetchCompare={(miljo, item) =>
                OrganisasjonService.fetchOrganisasjon(item.orgnummer, miljo).then(
                  (response) => !OrganisasjonComperator.compare(item, response).isMismatch
                )
              }
              items={items}
            />
          )}
        />
      )}
    </Form>
  );
};
