import React, { useState } from 'react';
import { Pagination } from '@navikt/ds-react';

export type PaginationProps<T> = {
  items: T[];
  render: (items: T[], index: number) => any;
  itemsPerPage?: number;
};

const ITEMS_PER_PAGE = 10;

function Pageable<T>({ items, render, itemsPerPage = ITEMS_PER_PAGE }: PaginationProps<T>) {
  const [position, setPosition] = useState<number>(0);
  return (
    <>
      {render(
        items.slice(
          position * itemsPerPage,
          Math.min(position * itemsPerPage + itemsPerPage, items.length)
        ),
        position
      )}
      <Pagination
        count={items.length}
        page={position + 1}
        onPageChange={(value) => setPosition(value - 1)}
      />
    </>
  );
}

Pageable.displayName = 'Pageable';

export default Pageable;
