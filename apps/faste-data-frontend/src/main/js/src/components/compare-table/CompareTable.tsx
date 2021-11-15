import React, { useEffect, useState } from 'react';
import 'nav-frontend-tabell-style';
import { SuccessAlert, WarningAlert, LoadableComponent } from '@navikt/dolly-komponenter';
import { Link } from 'react-router-dom';

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

type Row = {
  id: string;
  miljo: string;
  component: React.ReactNode;
};

function CompareTable<T extends Item>({
  labels = { id: 'ID' },
  miljo,
  items,
  fetchCompare,
}: Props<T>) {
  const [rows, setRows] = useState<Row[]>();

  useEffect(() => {
    const rows = items.map((value) => ({
      id: value.id,
      miljo: miljo,
      component: (
        <LoadableComponent
          onFetch={() => fetchCompare(miljo, value)}
          render={(isEqual: boolean) =>
            isEqual ? <SuccessAlert label="Likt" /> : <WarningAlert label="Ulikt" />
          }
        />
      ),
    }));
    setRows(rows);
  }, [items]);

  return (
    <table className="tabell">
      <thead>
        <tr>
          <th>{labels.id}</th>
          <th>Miljø</th>
          <th>Status</th>
          <th>Diff</th>
        </tr>
      </thead>
      <tbody>
        {rows &&
          rows.map((row, index) => (
            <tr key={index}>
              <td>{row.id}</td>
              <td>{row.miljo}</td>
              <td>{row.component}</td>
              <td>
                <Link
                  target="_blank"
                  to={{
                    pathname: `/organisasjon/${row.id}/${miljo}`,
                  }}
                >
                  Se differanse
                </Link>
              </td>
            </tr>
          ))}
      </tbody>
    </table>
  );
}

export default CompareTable;
