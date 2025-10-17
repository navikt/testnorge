import React from 'react';
import ReactDatepicker, {registerLocale} from 'react-datepicker';
// @ts-ignore
import {addYears, subYears} from 'date-fns';
// @ts-ignore
import {nb} from 'date-fns/locale';
import 'react-datepicker/dist/react-datepicker.css';
import styled from 'styled-components';
import {InputFormItem} from '../form';

registerLocale('nb', nb);

type Props = {
    label?: string;
    name?: string;
    value: Date;
    placeholder?: string;
    onChange: (value: Date[] | null) => void;
    onBlur?: () => void;
    disabled?: boolean;
    excludeDates?: Date[];
    minDate?: Date;
    maxDate?: Date;
    className?: string;
};

const StyledInput = styled(InputFormItem)`
    margin-top: 5px;
    width: 100%;
`;

const StyledDatePicker = styled(ReactDatepicker)`
    && {
        padding-right: unset;
    }
`;

const TimePicker = ({
                        label,
                        name,
                        value,
                        placeholder = 'Ikke spesifisert',
                        onChange,
                        onBlur,
                        disabled = false,
                        excludeDates,
                        minDate,
                        maxDate,
                        className,
                    }: Props) => (
    <StyledDatePicker
        locale="nb"
        className={className}
        dateFormat="dd.MM.yyyy HH:mm"
        placeholderText={placeholder}
        selected={value instanceof Date ? value : null}
        onChange={onChange}
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
        customInput={<StyledInput label={label}/>}
        excludeDates={excludeDates}
    />
);

export default TimePicker;
