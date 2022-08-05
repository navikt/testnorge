import React, { ChangeEventHandler, useRef, useState } from 'react';
import { format, isValid, parse } from 'date-fns';
import FocusTrap from 'focus-trap-react';
import { DayPicker } from 'react-day-picker';
import { usePopper } from 'react-popper';
import styled from 'styled-components';
import { TextField } from '@navikt/ds-react';

type Props = {
  id: string;
  required?: boolean;
  label: string;
  onBlur: (value: string) => void;
  error?: string;
};

const DateField = styled(TextField)`
  width: 25%;
  padding-right: 10px;
`;

export default ({ id, label, onBlur, required = false, error, ...props }: Props) => {
  const [selected, setSelected] = useState<Date>();
  const [inputValue, setInputValue] = useState<string>('');
  const [isPopperOpen, setIsPopperOpen] = useState(false);

  const popperRef = useRef<HTMLDivElement>(null);
  const buttonRef = useRef<HTMLButtonElement>(null);
  const [popperElement, setPopperElement] = useState<HTMLDivElement | null>(null);

  const popper = usePopper(popperRef.current, popperElement, {
    placement: 'bottom-start',
  });

  const closePopper = () => {
    setIsPopperOpen(false);
    buttonRef?.current?.focus();
  };

  const handleInputChange: ChangeEventHandler<HTMLInputElement> = (e) => {
    setInputValue(e.currentTarget.value);
    const date = parse(e.currentTarget.value, 'y-MM-dd', new Date());
    if (isValid(date)) {
      setSelected(date);
    } else {
      setSelected(undefined);
    }
  };

  const handleButtonClick = () => {
    setIsPopperOpen(true);
  };

  const handleDaySelect = (date: Date) => {
    setSelected(date);
    if (date) {
      setInputValue(format(date, 'y-MM-dd'));
      onBlur(date.toISOString());
      closePopper();
    } else {
      setInputValue('');
    }
  };

  return (
    <div>
      <div ref={popperRef}>
        <DateField
          label={label}
          placeholder={format(new Date(), 'y-MM-dd')}
          value={inputValue}
          onChange={handleInputChange}
        />
        <button
          ref={buttonRef}
          type="button"
          className="pa2 bg-white button-reset ba"
          aria-label={label}
          onClick={handleButtonClick}
        >
          <span role="img" aria-label="kalender icon">
            📅
          </span>
        </button>
      </div>
      {isPopperOpen && (
        <FocusTrap
          active
          focusTrapOptions={{
            initialFocus: false,
            allowOutsideClick: true,
            clickOutsideDeactivates: true,
            onDeactivate: closePopper,
          }}
        >
          <div
            tabIndex={-1}
            style={popper.styles.popper}
            className="dialog-sheet"
            {...popper.attributes.popper}
            ref={setPopperElement}
            role="dialog"
          >
            <DayPicker
              initialFocus={isPopperOpen}
              mode="single"
              defaultMonth={selected}
              selected={selected}
              required={required}
              onSelect={handleDaySelect}
              {...props}
            />
          </div>
        </FocusTrap>
      )}
    </div>
  );
};
