import React, { useState } from 'react';
import { Button, InlineMessage, TextField } from '@navikt/ds-react';
import {
  SearchButtonSlot,
  SearchFieldSlot,
  SearchRow,
  SearchStatusSlot,
} from '@/pages/endringsmelding-page/form/FormLayout';

type Props = {
  setMiljoer: (value: string[]) => void;
  setShow: (show: boolean) => void;
  labels: {
    label: string;
    button: string;
    onFound: string;
    onNotFound: string;
    onError: string;
    syntIdent: string;
  };
  onChange?: (value: string) => void;
};

type SearchStatus = {
  status: 'success' | 'warning' | 'error';
  message: string;
};

const isSyntheticIdent = (value: string) => /^[0-9]{2}[4-9]{1}[0-9]{8}$/.test(value);
const isValidIdent = (value: string) => /^[0-9]{11}$/.test(value);

export const Search = ({ labels, onChange, setShow, setMiljoer }: Props) => {
  const [value, setValue] = useState('');
  const [loading, setLoading] = useState(false);
  const [status, setStatus] = useState<SearchStatus | null>(null);

  const hentMiljoeInfo = async () => {
    if (isSyntheticIdent(value)) {
      setStatus({ status: 'warning', message: labels.syntIdent });
      setShow(false);
      setMiljoer([]);
      return;
    }

    if (!isValidIdent(value)) {
      setStatus({ status: 'warning', message: 'Ident må være 11 siffer.' });
      setShow(false);
      setMiljoer([]);
      return;
    }

    setLoading(true);
    setStatus(null);

    try {
      const response = await fetch(`/endringsmelding-service/api/v1/ident/miljoer`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ ident: value }),
      });

      if (!response.ok) {
        throw new Error();
      }

      const data = await response.json();
      const miljoer = data?.[0]?.miljoer ?? [];
      setMiljoer(miljoer);

      if (miljoer.length > 0) {
        setShow(true);
        setStatus({ status: 'success', message: labels.onFound });
        return;
      }

      setShow(false);
      setStatus({ status: 'warning', message: labels.onNotFound });
    } catch {
      setMiljoer([]);
      setShow(false);
      setStatus({ status: 'error', message: labels.onError });
    } finally {
      setLoading(false);
    }
  };

  return (
    <SearchRow>
      <SearchFieldSlot>
        <TextField
          label={labels.label}
          autoComplete="off"
          inputMode="numeric"
          value={value}
          onChange={(event) => {
            const nextValue = event.target.value;
            setShow(false);
            setMiljoer([]);
            setStatus(
              isSyntheticIdent(nextValue) ? { status: 'warning', message: labels.syntIdent } : null,
            );
            onChange?.(nextValue);
            setValue(nextValue);
          }}
          onKeyDown={(event) => {
            if (event.key === 'Enter') {
              event.preventDefault();
              void hentMiljoeInfo();
            }
          }}
        />
      </SearchFieldSlot>
      <SearchButtonSlot>
        <Button
          type="button"
          onClick={() => {
            void hentMiljoeInfo();
          }}
          disabled={loading || value === '' || isSyntheticIdent(value)}
          loading={loading}
        >
          {labels.button}
        </Button>
      </SearchButtonSlot>
      <SearchStatusSlot>
        {status && (
          <InlineMessage
            status={status.status}
            size="small"
            role={status.status === 'error' ? 'alert' : 'status'}
          >
            {status.message}
          </InlineMessage>
        )}
      </SearchStatusSlot>
    </SearchRow>
  );
};
