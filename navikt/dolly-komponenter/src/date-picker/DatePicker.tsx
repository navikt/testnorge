import React, { ChangeEventHandler, useRef, useState } from 'react';
import { format, isValid, parse } from 'date-fns';
import { DayPicker } from 'react-day-picker';
import { usePopper } from 'react-popper';
import styled from 'styled-components';
import { TextField } from '@navikt/ds-react';
import 'react-day-picker/dist/style.css';
import { ChildrenBlur } from './ChildrenBlur';

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

const FlexDiv = styled.div`
  display: flex;
`;

const CalendarSvg = styled.span`
  position: relative;
  height: 0;
  width: 0;
  pointer-events: none;
  right: 50px;
  top: 45px;
`;

const StyledDaypicker = styled(DayPicker)`
  .rdp-months {
    border: thin solid;
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
      setSelected(undefined as unknown as Date);
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
      <ChildrenBlur onBlur={closePopper}>
        <FlexDiv ref={popperRef}>
          <DateField
            label={label}
            placeholder={format(new Date(), 'y-MM-dd')}
            value={inputValue}
            onChange={handleInputChange}
            error={required && !selected ? 'Påkrevd' : null}
            onClick={() => setIsPopperOpen(true)}
          />
          <CalendarSvg role="img" aria-label="kalender icon">
            📅
          </CalendarSvg>
        </FlexDiv>
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
              defaultMonth={selected}
              onDayClick={handleDaySelect}
            />
          </div>
        )}
      </ChildrenBlur>
    </div>
  );
};
