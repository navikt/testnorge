import {
  Button,
  Heading,
  InlineMessage,
  Pagination,
  Select,
  TextField,
} from '@navikt/ds-react';
import React, { useEffect, useState } from 'react';
import { PersonFasteDataService, PersonService } from '@/service';
import { CompareTable } from '@/components/compare-table';
import { PersonComperator } from '@/comperator';
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

const miljoer = ['q1', 'q2', 'q4', 'qx'];

const toOptions = (options: string[]) =>
  options.map((value) => ({
    value: value,
    label: value.toUpperCase(),
  }));

const ITEMS_PER_PAGE = 10;

const FastePersonDataPage = () => {
  const [personer, setPersoner] = useState<Person[] | null>(null);
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
    PersonFasteDataService.fetchPersoner(
      value,
      tag || undefined,
      opprinnelse || undefined,
    )
      .then((response) => {
        setPersoner(response);
        setLoading(false);
      })
      .catch(() => {
        setError(true);
        setLoading(false);
      });
  };

  useEffect(() => {
    setPersoner(null);
    setError(false);
    setPage(1);
  }, [gruppe, miljo, tag, opprinnelse]);

  useEffect(() => {
    if (!personer?.length) {
      setPage(1);
      return;
    }

    const pageCount = Math.ceil(personer.length / ITEMS_PER_PAGE);
    setPage((currentPage) => Math.min(currentPage, pageCount));
  }, [personer]);

  const items = personer?.slice(
    (page - 1) * ITEMS_PER_PAGE,
    Math.min((page - 1) * ITEMS_PER_PAGE + ITEMS_PER_PAGE, personer.length)
  );
  const pageCount = personer?.length ? Math.ceil(personer.length / ITEMS_PER_PAGE) : 0;

  return (
    <AppPage>
      <SectionStack>
        <Heading level="1" size="large">
          Person Faste Data
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
        {personer !== null && personer.length === 0 && (
          <InlineMessage role="status" status="warning" size="small">
            Ingen treff.
          </InlineMessage>
        )}
        {items && items.length > 0 && (
          <ResultsStack>
            <CompareTable
              miljo={miljo}
              fetchCompare={(_miljo, item) =>
                PersonService.fetchPerson(item.ident, _miljo).then(
                  (response) => !PersonComperator.compare(item, response).isMismatch
                )
              }
              items={items.map((person) => ({
                ...person,
                id: person.ident,
              }))}
            />
            <PaginationRow>
              <Pagination
                count={pageCount}
                onPageChange={setPage}
                page={page}
                srHeading={{ tag: 'h2', text: 'Sider for personresultater' }}
              />
            </PaginationRow>
          </ResultsStack>
        )}
      </SectionStack>
    </AppPage>
  );
};

FastePersonDataPage.displayName = 'FastePersonDataPage';

export default FastePersonDataPage;
