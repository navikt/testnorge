import React, { useState } from 'react';

import Pagination from 'paginering';

export type PaginationProps<T> = {
  items: T[];
  render: (items: T[], index: number) => any;
};

const ITEMS_PER_PAGE = 10;

function Pageable<T>({ items, render }: PaginationProps<T>) {
  const [position, setPosition] = useState<number>(0);
  return (
    <>
      {render(
        items.slice(
          position * ITEMS_PER_PAGE,
          Math.min(position * ITEMS_PER_PAGE + ITEMS_PER_PAGE, items.length)
        ),
        position
      )}
      <Pagination
        itemsPerPage={ITEMS_PER_PAGE}
        numberOfItems={items.length}
        currentPage={position + 1}
        onChange={(value) => setPosition(value - 1)}
      />
    </>
  );
}

Pageable.displayName = 'Pageable';

export default Pageable;
