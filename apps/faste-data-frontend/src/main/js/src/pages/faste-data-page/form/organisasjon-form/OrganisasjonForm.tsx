import { Form, Line, SelectFormItem } from '@/components/form';
import React, { useEffect, useState } from 'react';
import { OrganisasjonFasteDataService, OrganisasjonService } from '@/service';
import {
  Adresse as FasteDataAdresse,
  Organisasjon as FasteDataOrganisasjon,
} from '@/service/OrganisasjonFasteDataService';
import { Knapp } from '@/components/knapp';
import { CompareTable } from '@/components/compare-table';
import { Adresse, Organisasjon } from '@/service/OrganisasjonService';
import { OrganisasjonComperator } from '@/comperator';
import { Pageable } from '@/components/pagable';

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

  const onSearch = (gruppe: Gruppe) => {
    setLoading(true);
    OrganisasjonFasteDataService.fetchOrganisasjoner(gruppe)
      .then((response) => {
        setOrganisasjoner(response);
        setLoading(false);
      })
      .catch(() => setLoading(false));
  };

  useEffect(() => {
    setOrganisasjoner(null);
  }, [gruppe, miljo]);

  return (
    <Form>
      <Line>
        <SelectFormItem
          label="Grupp"
          htmlId="gruppe-selec"
          onChange={(value: Gruppe) => setGruppe(value)}
          options={toOptions(grupper)}
        />
        <SelectFormItem
          label="Miljo"
          htmlId="miljo-select"
          onChange={(value: string) => setMiljo(value)}
          options={toOptions(miljoer)}
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
