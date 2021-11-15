import { Form, Knapp, Line, Page, Pageable, SelectFormItem } from '@navikt/dolly-komponenter';
import React, { useEffect, useState } from 'react';
import { PersonFasteDataService, PersonService } from '@/service';
import { CompareTable } from '@/components/compare-table';
import { Input } from 'nav-frontend-skjema';
import { PersonComperator } from '@/comperator';

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

const FastePersonDataPage = () => {
  const [personer, setPersoner] = useState<Person[]>();
  const [gruppe, setGruppe] = useState<Gruppe>('DOLLY');
  const [miljo, setMiljo] = useState<string>('q1');
  const [loading, setLoading] = useState<boolean>(false);
  const [tag, setTag] = useState<string>('');
  const [opprinnelse, setOpprinnelse] = useState<string>('');

  const onSearch = (value: Gruppe) => {
    setLoading(true);
    PersonFasteDataService.fetchPersoner(
      value,
      !tag ? null : tag,
      !opprinnelse ? null : opprinnelse
    )
      .then((response) => {
        setPersoner(response);
        setLoading(false);
      })
      .catch(() => setLoading(false));
  };

  useEffect(() => {
    setPersoner(null);
  }, [gruppe, miljo, tag, opprinnelse]);

  return (
    <Page>
      <h1>Person Faste Data</h1>
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
        {personer && (
          <Pageable
            items={personer.map((person) => ({
              ...person,
              id: person.ident,
            }))}
            render={(items, index) => (
              <CompareTable
                key={index}
                miljo={miljo}
                fetchCompare={(_miljo, item) =>
                  PersonService.fetchPerson(item.ident, _miljo).then(
                    (response) => !PersonComperator.compare(item, response).isMismatch
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

FastePersonDataPage.displayName = 'FastePersonDataPage';

export default FastePersonDataPage;
