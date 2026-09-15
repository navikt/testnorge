import React from 'react';
import { Table } from '@navikt/ds-react';
import styled from 'styled-components';
import { ResponsiveSplit, ScrollArea } from '@/components/layout';

type Mismatch = {
  left: Field;
  right: Field;
};

type Field = {
  key: string;
  value: unknown;
};

type Props = {
  labels: {
    left: string;
    right: string;
  };
  mismatch: Mismatch[];
};

const renderValue = (value: unknown) =>
  typeof value === 'string' ? value : JSON.stringify(value, null, 2);

const Value = styled.span`
  display: block;
  white-space: pre-wrap;
  word-break: break-word;
`;

export default ({ labels, mismatch }: Props) => {
  return (
    <ResponsiveSplit>
      <ScrollArea>
        <Table aria-label={labels.left} size="small">
          <Table.Header>
            <Table.Row>
              <Table.HeaderCell scope="col">{labels.left}</Table.HeaderCell>
              <Table.HeaderCell scope="col">Verdi</Table.HeaderCell>
            </Table.Row>
          </Table.Header>
          <Table.Body>
            {mismatch.map((row, index) => (
              <Table.Row key={`${row.left.key}-${index}`}>
                <Table.HeaderCell scope="row">{row.left.key}</Table.HeaderCell>
                <Table.DataCell>
                  <Value>{renderValue(row.left.value)}</Value>
                </Table.DataCell>
              </Table.Row>
            ))}
          </Table.Body>
        </Table>
      </ScrollArea>
      <ScrollArea>
        <Table aria-label={labels.right} size="small">
          <Table.Header>
            <Table.Row>
              <Table.HeaderCell scope="col">{labels.right}</Table.HeaderCell>
              <Table.HeaderCell scope="col">Verdi</Table.HeaderCell>
            </Table.Row>
          </Table.Header>
          <Table.Body>
            {mismatch.map((row, index) => (
              <Table.Row key={`${row.right.key}-${index}`}>
                <Table.HeaderCell scope="row">{row.right.key}</Table.HeaderCell>
                <Table.DataCell>
                  <Value>{renderValue(row.right.value)}</Value>
                </Table.DataCell>
              </Table.Row>
            ))}
          </Table.Body>
        </Table>
      </ScrollArea>
    </ResponsiveSplit>
  );
};
