import React, { ChangeEventHandler, useRef, useState } from 'react';
import { format, isValid, parse } from 'date-fns';
import { DayPicker } from 'react-day-picker';
import { usePopper } from 'react-popper';
import styled from 'styled-components';
import { TextField } from '@navikt/ds-react';
import 'react-day-picker/dist/style.css';

type Props = {
  id: string;
  required?: boolean;
  label: string;
  onBlur: (value: string) => void;
  error?: string;
};

const DateField = styled(TextField)`
  padding-right: 10px;
`;

const CalendarSvg = styled.span`
  position: relative;
  pointer-events: none;
  right: -155px;
  top: -35px;
`;

const StyledDaypicker = styled(DayPicker)`
  .rdp-months {
    border: double;
    background-color: hsl(0deg 0% 96%);
  }
`;

export default ({ label, onBlur, required = false }: Props) => {
  const [selected, setSelected] = useState<Date>(new Date());
  const [inputValue, setInputValue] = useState<string>('');
  const [isPopperOpen, setIsPopperOpen] = useState(false);

  const popperRef = useRef<HTMLDivElement>(null);
  const [popperElement, setPopperElement] = useState<HTMLDivElement | null>(null);

  const popper = usePopper(popperRef.current, popperElement, {
    placement: 'bottom-start',
  });

  const closePopper = () => {
    setTimeout(() => setIsPopperOpen(false), 250);
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
          error={required && !selected ? 'Påkrevd' : null}
          onClick={() => setIsPopperOpen(true)}
          onBlur={closePopper}
        />
        <CalendarSvg role="img" aria-label="kalender icon">
          📅
        </CalendarSvg>
      </div>
      {isPopperOpen && (
        <div
          tabIndex={-1}
          style={popper.styles.popper}
          className="dialog-sheet"
          {...popper.attributes.popper}
          ref={setPopperElement}
          role="dialog"
        >
          <StyledDaypicker
            mode="single"
            selected={selected}
            required={required}
            defaultMonth={selected}
            onSelect={handleDaySelect}
          />
        </div>
      )}
    </div>
  );
};