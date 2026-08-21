import {
  Button,
  Heading,
  InlineMessage,
  Pagination,
  Select,
  TextField,
} from '@navikt/ds-react';
import React, { useEffect, useState } from 'react';
import { OrganisasjonFasteDataService, OrganisasjonService } from '@/service';
import { Organisasjon as FasteDataOrganisasjon } from '@/service/OrganisasjonFasteDataService';
import { CompareTable } from '@/components/compare-table';
import { OrganisasjonComperator } from '@/comperator';
import { CodeSearch } from '@/components/CodeSearch';
import {
  AppPage,
  ButtonField,
  FormRow,
  FormStack,
  HalfField,
  PaginationRow,
  QuarterField,
  ResultsStack,
  SectionStack,
} from '@/components/layout';

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

const miljoer = ['q1', 'q2', 'q4'];

const toOptions = (options: string[]) =>
  options.map((value) => ({
    value: value,
    label: value.toUpperCase(),
  }));

const ITEMS_PER_PAGE = 10;

const FasteOrganisasjonDataPage = () => {
  const [organisasjoner, setOrganisasjoner] = useState<FasteDataOrganisasjon[] | null>(null);
  const [gruppe, setGruppe] = useState<Gruppe>('DOLLY');
  const [miljo, setMiljo] = useState<string>('q1');
  const [loading, setLoading] = useState<boolean>(false);
  const [tag, setTag] = useState<string>('');
  const [opprinnelse, setOpprinnelse] = useState<string>('');
  const [error, setError] = useState<boolean>(false);
  const [page, setPage] = useState<number>(1);

  const onSearch = (value: Gruppe) => {
    setLoading(true);
    setError(false);
    OrganisasjonFasteDataService.fetchOrganisasjoner(
      value,
      tag || undefined,
      opprinnelse || undefined,
    )
      .then((response) => {
        setOrganisasjoner(response);
        setLoading(false);
      })
      .catch(() => {
        setError(true);
        setLoading(false);
      });
  };

  useEffect(() => {
    setOrganisasjoner(null);
    setError(false);
    setPage(1);
  }, [gruppe, miljo, tag, opprinnelse]);

  useEffect(() => {
    if (!organisasjoner?.length) {
      setPage(1);
      return;
    }

    const pageCount = Math.ceil(organisasjoner.length / ITEMS_PER_PAGE);
    setPage((currentPage) => Math.min(currentPage, pageCount));
  }, [organisasjoner]);

  const items = organisasjoner?.slice(
    (page - 1) * ITEMS_PER_PAGE,
    Math.min((page - 1) * ITEMS_PER_PAGE + ITEMS_PER_PAGE, organisasjoner.length)
  );
  const pageCount = organisasjoner?.length ? Math.ceil(organisasjoner.length / ITEMS_PER_PAGE) : 0;

  return (
    <AppPage>
      <SectionStack>
        <Heading level="1" size="large">
          Organisasjon Faste Data
        </Heading>
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
        <Heading level="2" size="medium">
          Gruppe søk
        </Heading>
        <form
          onSubmit={(event) => {
            event.preventDefault();
            onSearch(gruppe);
          }}
        >
          <FormStack>
            <FormRow>
              <QuarterField>
                <Select
                  label="Gruppe"
                  value={gruppe}
                  onChange={(event) => setGruppe(event.currentTarget.value as Gruppe)}
                >
                  {toOptions(grupper).map((option) => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </Select>
              </QuarterField>
              <QuarterField>
                <Select
                  label="Miljø"
                  value={miljo}
                  onChange={(event) => setMiljo(event.currentTarget.value)}
                >
                  {toOptions(miljoer).map((option) => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </Select>
              </QuarterField>
              <HalfField>
                <TextField
                  label="Tag"
                  onChange={(event) => {
                    setTag(event.target.value);
                  }}
                  value={tag}
                />
              </HalfField>
            </FormRow>
            <FormRow>
              <HalfField>
                <TextField
                  label="Opprinnelse"
                  onChange={(event) => {
                    setOpprinnelse(event.target.value);
                  }}
                  value={opprinnelse}
                />
              </HalfField>
              <ButtonField>
                <Button disabled={loading} loading={loading} type="submit" variant="secondary">
                  Søk
                </Button>
              </ButtonField>
            </FormRow>
            {error && (
              <InlineMessage role="alert" status="error" size="small">
                Noe gikk galt under søket.
              </InlineMessage>
            )}
          </FormStack>
        </form>
        {organisasjoner !== null && organisasjoner.length === 0 && (
          <InlineMessage role="status" status="warning" size="small">
            Ingen treff.
          </InlineMessage>
        )}
        {items && items.length > 0 && (
          <ResultsStack>
            <CompareTable
              miljo={miljo}
              fetchCompare={(_miljo, item) =>
                OrganisasjonService.fetchOrganisasjon(item.orgnummer, _miljo).then(
                  (response) => !OrganisasjonComperator.compare(item, response).isMismatch
                )
              }
              items={items.map((organisasjon) => ({
                ...organisasjon,
                id: organisasjon.orgnummer,
              }))}
            />
            <PaginationRow>
              <Pagination
                count={pageCount}
                onPageChange={setPage}
                page={page}
                srHeading={{ tag: 'h2', text: 'Sider for organisasjonsresultater' }}
              />
            </PaginationRow>
          </ResultsStack>
        )}
      </SectionStack>
    </AppPage>
  );
};

FasteOrganisasjonDataPage.displayName = 'FasteOrganiasjonDataPage';

export default FasteOrganisasjonDataPage;
