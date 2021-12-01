import React from 'react';
import ReactDatepicker, { registerLocale } from 'react-datepicker';
// @ts-ignore
import { addYears, subYears } from 'date-fns';
// @ts-ignore
import locale_nb from 'date-fns/locale/nb';
import 'react-datepicker/dist/react-datepicker.css';
import { Input } from 'nav-frontend-skjema';
import styled from 'styled-components';

registerLocale('nb', locale_nb);

const StyledInput = styled(Input)`
  margin-top: 5px;
`;

export const TimePicker = ({
  // @ts-ignore
  label,
  // @ts-ignore
  name,
  // @ts-ignore
  value,
  // @ts-ignore
  placeholder = 'Ikke spesifisert',
  // @ts-ignore
  onChange,
  // @ts-ignore
  onBlur,
  disabled = false,
  // @ts-ignore
  feil,
  // @ts-ignore
  excludeDates,
  // @ts-ignore
  minDate,
  // @ts-ignore
  maxDate,
  // @ts-ignore
  className,
}) => {
  return (
    <ReactDatepicker
      locale="nb"
      className={className}
      dateFormat="dd.MM.yyyy HH:mm"
      placeholderText={placeholder}
      selected={(value && new Date(value)) || null}
      onChange={onChange}
      showMonthDropdown
      showYearDropdown
      showTimeInput
      timeInputLabel={'Tid'}
      minDate={minDate || subYears(new Date(), 100)}
      maxDate={maxDate || addYears(new Date(), 5)}
      dropdownMode="select"
      disabled={disabled}
      onBlur={onBlur}
      name={name}
      id={name}
      autoComplete="off"
      customInput={<StyledInput label={label} />}
      excludeDates={excludeDates}
    />
  );
};

export default TimePicker;
