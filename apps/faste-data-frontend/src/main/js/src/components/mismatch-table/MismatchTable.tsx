import React from 'react';
import 'nav-frontend-tabell-style';
import styled from 'styled-components';
import { Input as NavInput } from 'nav-frontend-skjema';

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

const Table = styled.table`
  width: 50%;
`;

const MismatchTable = styled.div`
  display: flex;
  margin: 1px;
`;

export default ({ labels, mismatch }: Props) => {
  return (
    <MismatchTable>
      <Table className="tabell">
        <thead>
          <tr>
            <th>{labels.left}</th>
            <th>Verdi</th>
          </tr>
        </thead>
        <tbody>
          {mismatch.map((row, index) => (
            <tr key={index}>
              <td>{row.left.key}</td>
              <td>{row.left.value}</td>
            </tr>
          ))}
        </tbody>
      </Table>
      <Table className="tabell">
        <thead>
          <tr>
            <th>{labels.right}</th>
            <th>Verdi</th>
          </tr>
        </thead>
        <tbody>
          {mismatch.map((row, index) => (
            <tr key={index}>
              <td>{row.right.key}</td>
              <td>{row.right.value}</td>
            </tr>
          ))}
        </tbody>
      </Table>
    </MismatchTable>
  );
};
