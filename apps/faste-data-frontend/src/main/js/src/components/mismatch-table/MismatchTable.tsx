import React from 'react';
import 'nav-frontend-tabell-style';

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

export default ({ labels, mismatch }: Props) => {
  return (
    <table className="tabell">
      <thead>
        <tr>
          <th>{labels.left}</th>
          <th>Verdi</th>
          <th>{labels.right}</th>
          <th>Verdi</th>
        </tr>
      </thead>
      <tbody>
        {mismatch.map((row, index) => (
          <tr key={index}>
            <td>{row.left.key}</td>
            <td>{row.left.value}</td>
            <td>{row.right.key}</td>
            <td>{row.right.value}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};
