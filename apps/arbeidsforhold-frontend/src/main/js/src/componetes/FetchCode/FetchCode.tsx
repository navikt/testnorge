import React, { useEffect, useState } from "react";
import { CodeView } from "@/componetes";
import NavFrontendSpinner from "nav-frontend-spinner";
import Pagination from "paginering";

import "./FetchCode.less";

type Props = {
  fetchFromPosition: (position: number) => Promise<Response>;
};

export const FetchCode = ({ fetchFromPosition }: Props) => {
  const [position, setPosition] = useState(0);
  const [code, setCode] = useState<string>();
  const [totalItems, setTotalItems] = useState<number>();
  const [loading, setLoading] = useState(true);

  const getCode = (value: number) => {
    fetchFromPosition(value)
      .then((response) => {
        setTotalItems(+response.headers.get("number-of-elements"));
        return response.text();
      })
      .then((content) => {
        setCode(content);
        setLoading(false);
      });
  };

  useEffect(() => {
    setLoading(true);
    setPosition(0);
  }, [fetchFromPosition]);
  useEffect(() => getCode(position), [position, fetchFromPosition]);

  const onNext = () => setPosition(position + 1);
  const onPrevious = () => setPosition(position - 1);

  return (
    <div className="fetch-code">
      {loading ? (
        <div className="fetch-code__spinner">
          <NavFrontendSpinner type="XXL" aria-label="Laster inn..." />
        </div>
      ) : (
        <>
          <CodeView
            onPrevious={position > 0 && totalItems !== 0 ? onPrevious : null}
            onNext={
              position < totalItems - 1 && totalItems !== 0 ? onNext : null
            }
            code={code}
          >
            <div className="fetch-code__paging">
              <Pagination
                itemsPerPage={1}
                numberOfItems={totalItems}
                currentPage={position + 1}
                onChange={(value) => setPosition(value - 1)}
              />
            </div>
          </CodeView>
        </>
      )}
    </div>
  );
};
