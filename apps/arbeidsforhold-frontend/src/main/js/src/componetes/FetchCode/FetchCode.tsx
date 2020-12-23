import React, { useEffect, useState } from "react";
import { CodeView } from "@/componetes";
import NavFrontendSpinner from "nav-frontend-spinner";

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

  useEffect(() => getCode(position), [position, fetchFromPosition]);

  const onNext = () => setPosition(position + 1);
  const onPrevious = () => setPosition(position - 1);

  return (
    <div>
      {loading ? (
        <NavFrontendSpinner type="XL" aria-label="Laster inn..." />
      ) : (
        <CodeView
          onPrevious={position > 0 && totalItems !== 0 ? onPrevious : null}
          onNext={position < totalItems && totalItems !== 0 - 1 ? onNext : null}
          code={code}
        />
      )}
    </div>
  );
};
