import React, { useState } from 'react';
import { Button, HStack, InlineMessage, TextField, VStack } from '@navikt/ds-react';
import styled from 'styled-components';
import isNotFoundError from '@/isNotFoundError';

const GruppeSearch = styled.div`
  width: 100%;
`;

type Props<T> = {
  onSearch: (value: string) => Promise<T>;
  labels: {
    label: string;
    button: string;
    onFound: string;
    onNotFound: string;
    onError: string;
  };
  onChange?: (value: string) => void;
};

export default <T extends unknown>({ labels, onSearch, onChange }: Props<T>) => {
  const [value, setValue] = useState('');
  const [status, setStatus] = useState<'error' | 'loading' | 'not-found' | 'success' | null>(
    null
  );

  const _onSearch = (search: string) => {
    if (search.trim() === '') {
      setStatus(null);
      return Promise.resolve(undefined);
    }

    setStatus('loading');
    return onSearch(search)
      .then((response) => {
        setStatus('success');
        return response;
      })
      .catch((error) => {
        if (isNotFoundError(error)) {
          setStatus('not-found');
        } else {
          setStatus('error');
        }
      });
  };

  const renderStatus = () => {
    switch (status) {
      case 'error':
        return (
          <InlineMessage status="error" size="small">
            {labels.onError}
          </InlineMessage>
        );
      case 'loading':
        return (
          <InlineMessage status="info" size="small">
            Laster søk...
          </InlineMessage>
        );
      case 'not-found':
        return (
          <InlineMessage status="warning" size="small">
            {labels.onNotFound}
          </InlineMessage>
        );
      case 'success':
        return (
          <InlineMessage status="success" size="small">
            {labels.onFound}
          </InlineMessage>
        );
    }
  };

  return (
    <GruppeSearch>
      <VStack gap="space-8">
        <HStack gap="space-8" align="end">
          <TextField
            label={labels.label}
            onChange={(event) => {
              if (onChange) {
                onChange(event.target.value);
              }
              setValue(event.target.value);
            }}
            value={value}
            style={{ flexGrow: 1 }}
          />
          <Button
            disabled={status === 'loading'}
            loading={status === 'loading'}
            onClick={() => _onSearch(value)}
            variant="secondary"
          >
            {labels.button}
          </Button>
        </HStack>
        {renderStatus()}
      </VStack>
    </GruppeSearch>
  );
};
