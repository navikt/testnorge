import { Form, Knapp, Line, Page, Pageable, SelectFormItem } from '@navikt/dolly-komponenter';
import React, { useEffect, useState } from 'react';
import { OrganisasjonFasteDataService, OrganisasjonService } from '@/service';
import { Organisasjon as FasteDataOrganisasjon } from '@/service/OrganisasjonFasteDataService';
import { CompareTable } from '@/components/compare-table';
import { OrganisasjonComperator } from '@/comperator';
import { Input } from 'nav-frontend-skjema';
import { CodeSearch } from '@/components/CodeSearch';

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

const FasteOrganisasjonDataPage = () => {
  const [organisasjoner, setOrganisasjoner] = useState<FasteDataOrganisasjon[]>();
  const [gruppe, setGruppe] = useState<Gruppe>('DOLLY');
  const [miljo, setMiljo] = useState<string>('q1');
  const [loading, setLoading] = useState<boolean>(false);
  const [tag, setTag] = useState<string>('');
  const [opprinnelse, setOpprinnelse] = useState<string>('');

  const onSearch = (value: Gruppe) => {
    setLoading(true);
    OrganisasjonFasteDataService.fetchOrganisasjoner(
      value,
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
    <Page>
      <h1>Organisasjon Faste Data</h1>
      <CodeSearch
        labels={{
          input: 'Søk etter organisasjon i faste data',
          button: 'Søk',
        }}
        onSearch={(value) =>
          OrganisasjonFasteDataService.fetchOrganisasjon(value).then((response) =>
            JSON.stringify(response, null, 2)
          )
        }
      />
      <h2>Gruppe søk</h2>
      <Form>
        <Line>
          <SelectFormItem
            label="Gruppe"
            htmlId="gruppe-select"
            // @ts-ignore
            onChange={(value: Gruppe) => setGruppe(value)}
            options={toOptions(grupper)}
          />
          <SelectFormItem
            label="Miljø"
            htmlId="miljo-select"
            // @ts-ignore
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
                fetchCompare={(_miljo, item) =>
                  OrganisasjonService.fetchOrganisasjon(item.orgnummer, _miljo).then(
                    (response) => !OrganisasjonComperator.compare(item, response).isMismatch
                  )
                }
                items={items}
              />
            )}
          />
        )}
      </Form>
    </Page>
  );
};

FasteOrganisasjonDataPage.displayName = 'FasteOrganiasjonDataPage';

export default FasteOrganisasjonDataPage;
