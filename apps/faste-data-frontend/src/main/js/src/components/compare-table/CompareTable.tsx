import React, { useEffect, useState } from 'react';
import { InlineMessage, Link, Loader, Table } from '@navikt/ds-react';
import { Link as RouterLink } from 'react-router';
import isNotFoundError from '@/isNotFoundError';
import { ScrollArea } from '@/components/layout';

type Item = {
  id: string;
};

type Props<T extends Item> = {
  items: T[];
  labels?: {
    id?: string;
  };
  miljo: string;
  fetchCompare: (miljo: string, item: T) => Promise<boolean>;
};

const CompareStatusCell = <T extends Item>({
  fetchCompare,
  item,
  miljo,
}: Pick<Props<T>, 'fetchCompare' | 'miljo'> & {
  item: T;
}) => {
  const [status, setStatus] = useState<'error' | 'loading' | 'not-found' | 'success' | 'warning'>(
    'loading'
  );
  const itemSignature = JSON.stringify(item);

  useEffect(() => {
    let active = true;
    setStatus('loading');

    fetchCompare(miljo, item)
      .then((isEqual) => {
        if (!active) {
          return;
        }
        setStatus(isEqual ? 'success' : 'warning');
      })
      .catch((error) => {
        if (!active) {
          return;
        }
        if (isNotFoundError(error)) {
          setStatus('not-found');
        } else {
          setStatus('error');
        }
      });

    return () => {
      active = false;
    };
  }, [fetchCompare, itemSignature, miljo]);

  switch (status) {
    case 'error':
      return (
        <InlineMessage status="error" size="small">
          Noe gikk galt
        </InlineMessage>
      );
    case 'not-found':
      return (
        <InlineMessage status="warning" size="small">
          Ikke funnet
        </InlineMessage>
      );
    case 'success':
      return (
        <InlineMessage status="success" size="small">
          Likt
        </InlineMessage>
      );
    case 'warning':
      return (
        <InlineMessage status="warning" size="small">
          Ulikt
        </InlineMessage>
      );
    default:
      return <Loader size="xsmall" title="Sammenligner" />;
  }
};

function CompareTable<T extends Item>({
  labels = { id: 'ID' },
  miljo,
  items,
  fetchCompare,
}: Props<T>) {
  return (
    <ScrollArea>
      <Table aria-label={`Sammenligning mot miljø ${miljo.toUpperCase()}`} size="small">
        <Table.Header>
          <Table.Row>
            <Table.HeaderCell scope="col">{labels.id}</Table.HeaderCell>
            <Table.HeaderCell scope="col">Miljø</Table.HeaderCell>
            <Table.HeaderCell scope="col">Status</Table.HeaderCell>
            <Table.HeaderCell scope="col">Diff</Table.HeaderCell>
          </Table.Row>
        </Table.Header>
        <Table.Body>
          {items.map((item) => (
            <Table.Row key={item.id}>
              <Table.HeaderCell scope="row">{item.id}</Table.HeaderCell>
              <Table.DataCell>{miljo.toUpperCase()}</Table.DataCell>
              <Table.DataCell>
                <CompareStatusCell fetchCompare={fetchCompare} item={item} miljo={miljo} />
              </Table.DataCell>
              <Table.DataCell>
                <Link
                  aria-label={`Se differanse for ${item.id} i miljø ${miljo.toUpperCase()}`}
                  as={RouterLink}
                  rel="noopener noreferrer"
                  target="_blank"
                  to={`/organisasjon/${item.id}/${miljo}`}
                >
                  Se differanse
                </Link>
              </Table.DataCell>
            </Table.Row>
          ))}
        </Table.Body>
      </Table>
    </ScrollArea>
  );
}

export default CompareTable;
