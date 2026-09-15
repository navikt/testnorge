import React, { FormEvent, useState } from 'react';
import { Button, InlineMessage, TextField, VStack } from '@navikt/ds-react';
import { NotFoundError } from '@navikt/dolly-lib';
import { ButtonField, SearchRow } from '@/components/layout';

export type Props<T> = {
  labels: {
    input: string;
    button: string;
  };
  onSearch: (value: string) => Promise<T>;
};

const Search = <T extends unknown>({ labels, onSearch }: Props<T>) => {
  const [value, setValue] = useState('');
  const [status, setStatus] = useState<'error' | 'loading' | 'not-found' | 'success' | null>(
    null
  );

  const renderStatus = () => {
    switch (status) {
      case 'error':
        return (
          <InlineMessage role="alert" status="error" size="small">
            Noe gikk galt.
          </InlineMessage>
        );
      case 'loading':
        return (
          <InlineMessage role="status" status="info" size="small">
            Laster søk...
          </InlineMessage>
        );
      case 'not-found':
        return (
          <InlineMessage role="status" status="warning" size="small">
            Ikke funnet.
          </InlineMessage>
        );
      case 'success':
        return (
          <InlineMessage role="status" status="success" size="small">
            Fant resultat.
          </InlineMessage>
        );
    }
  };

  const onSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setStatus(null);

    if (value.trim() === '') {
      return;
    }

    setStatus('loading');
    onSearch(value)
      .then(() => {
        setStatus('success');
      })
      .catch((error) => {
        if (error && error.name === NotFoundError.name) {
          setStatus('not-found');
        } else {
          setStatus('error');
        }
      });
  };

  return (
    <VStack gap="space-8">
      <form onSubmit={onSubmit}>
        <SearchRow>
          <TextField
            label={labels.input}
            value={value}
            onChange={(event) => {
              setValue(event.target.value);
            }}
          />
          <ButtonField>
            <Button
              disabled={status === 'loading'}
              loading={status === 'loading'}
              type="submit"
              variant="secondary"
            >
              {labels.button}
            </Button>
          </ButtonField>
        </SearchRow>
      </form>
      {renderStatus()}
    </VStack>
  );
};

Search.displayName = 'Search';

export default Search;
