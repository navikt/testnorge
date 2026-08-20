import React, { useEffect, useMemo, useState } from 'react';
import { Button, DatePicker, type DateValidationT, useDatepicker } from '@navikt/ds-react';
import { format, isValid, parse } from 'date-fns';
import styled from 'styled-components';

type Props = {
  id: string;
  label: string;
  value: string;
  onChange: (value: string) => void;
  error?: string;
  required?: boolean;
};

const INPUT_FORMAT = 'yyyy-MM-dd';
const FROM_DATE = new Date('1900-01-01T00:00:00');
const TO_DATE = new Date('2100-12-31T00:00:00');
const fieldGap = 'calc(var(--ax-space-8) + var(--ax-space-2))';

const DateFieldLayout = styled.div`
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  align-items: end;
  column-gap: ${fieldGap};
  width: 100%;

  @media (max-width: 48rem) {
    grid-template-columns: 1fr;
    row-gap: var(--ax-space-12);
  }
`;

const DateInputWrapper = styled.div`
  min-width: 0;
`;

const parseDateValue = (value: string) => {
  if (!value) {
    return undefined;
  }

  const parsedDate = parse(value, INPUT_FORMAT, new Date());
  return isValid(parsedDate) ? parsedDate : undefined;
};

const getValidationMessage = (validation: DateValidationT | null, required?: boolean) => {
  if (!validation || validation.isValidDate) {
    return undefined;
  }

  if (validation.isEmpty) {
    return required ? 'Påkrevd' : undefined;
  }

  if (validation.isInvalid) {
    return 'Ugyldig dato. Bruk formatet åååå-mm-dd.';
  }

  if (validation.isBefore || validation.isAfter) {
    return 'Dato må være mellom 1900-01-01 og 2100-12-31.';
  }

  return 'Ugyldig dato.';
};

export const ControlledDatePickerField = ({ id, label, value, onChange, error, required }: Props) => {
  const [validation, setValidation] = useState<DateValidationT | null>(null);
  const [showValidation, setShowValidation] = useState(false);
  const parsedValue = useMemo(() => parseDateValue(value), [value]);
  const { datepickerProps, inputProps, selectedDay, setSelected } = useDatepicker({
    defaultSelected: parsedValue,
    fromDate: FROM_DATE,
    toDate: TO_DATE,
    required,
    inputFormat: INPUT_FORMAT,
    allowTwoDigitYear: false,
    onDateChange: (date) => onChange(date ? format(date, INPUT_FORMAT) : ''),
    onValidate: (nextValidation) => setValidation(nextValidation),
  });

  useEffect(() => {
    const selectedValue = selectedDay ? format(selectedDay, INPUT_FORMAT) : '';

    if (!value) {
      if (selectedValue) {
        setSelected(undefined);
      }
      return;
    }

    if (parsedValue && value !== selectedValue) {
      setSelected(parsedValue);
    }
  }, [parsedValue, selectedDay, setSelected, value]);

  const clearDate = () => {
    setSelected(undefined);
    setShowValidation(false);
    onChange('');
  };

  return (
    <DateFieldLayout>
      <DateInputWrapper>
        <DatePicker {...datepickerProps} dropdownCaption>
          <DatePicker.Input
            {...inputProps}
            id={id}
            label={label}
            placeholder="åååå-mm-dd"
            error={error ?? (showValidation ? getValidationMessage(validation, required) : undefined)}
            onBlur={(event) => {
              inputProps.onBlur?.(event);
              setShowValidation(true);
            }}
          />
        </DatePicker>
      </DateInputWrapper>
      <Button type="button" variant="secondary" onClick={clearDate} disabled={!value}>
        Tøm
      </Button>
    </DateFieldLayout>
  );
};
