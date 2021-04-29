import React, { useEffect, useState } from 'react';
import { Loading } from '../loading';
import { NotFoundError } from '@navikt/dolly-lib';
import { ErrorAlert, WarningAlert } from '../alert';

type Props<T> = {
  onFetch: () => Promise<T>;
  render: (value: T) => any;
  onNotFound?: () => any;
};

function LoadableComponent<T>({ onFetch, render, onNotFound }: Props<T>) {
  const [loading, setLoading] = useState(true);
  const [data, setData] = useState<T>();
  const [notFound, setNotFound] = useState<boolean>(false);
  const [error, setError] = useState<boolean>(false);

  useEffect(() => {
    onFetch()
      .then((response) => {
        setData(response);
        setLoading(false);
      })
      .catch((e: Error) => {
        setLoading(false);
        if (e instanceof NotFoundError || e.name == 'NotFoundError') {
          setNotFound(true);
        } else {
          setError(true);
        }
      });
  }, []);

  if (notFound == true) {
    return onNotFound ? onNotFound() : <WarningAlert label="Ikke funnet" />;
  }
  if (error == true) {
    return <ErrorAlert label="Noe gikk galt" />;
  }

  if (loading) {
    return <Loading />;
  }
  return render(data);
}
export default LoadableComponent;
