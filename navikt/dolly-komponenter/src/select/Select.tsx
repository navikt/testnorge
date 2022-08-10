import { Label, Select as NavSelect } from '@navikt/ds-react';
import React from 'react';

import Select from 'react-select';
import styled from 'styled-components';

import './Select.less';

type Option = {
  value: string;
  label: string;
};

type Props = {
  htmlId: string;
  multi?: boolean;
  options: Option[];
  onChange: (value: string[]) => void;
  label: string;
  className?: string;
  error?: string;
};

const customStyles = {
  control: () => ({
    minHeight: '47px',
  }),
};

const StyledSelect = styled(Select)`
  margin-top: 7px;
  min-height: 47px;
`;

export default ({ multi, options, onChange, htmlId, label, error, ...props }: Props) => {
  if (multi) {
    return (
      <div {...props}>
        <Label>{label}</Label>
        <StyledSelect
          styles={customStyles}
          className={error ? 'skjemaelement__input--harFeil select--error' : 'select'}
          placeholder={'Velg...'}
          // @ts-ignore
          onChange={(value) => onChange(value.map((item) => item.value))}
          inputId={htmlId}
          isMulti={true}
          options={options}
        />
        {error && (
          <div role="alert" aria-live="assertive" className="skjemaelement__feilmelding">
            <label className="typo-feilmelding">{error}</label>
          </div>
        )}
      </div>
    );
  }

  return (
    <NavSelect
      id={htmlId}
      label={label}
      onChange={(value) => onChange([value.target.value])}
      error={error}
      {...props}
    >
      {options.map((option, index) => (
        <option key={index} value={option.value}>
          {option.label}
        </option>
      ))}
    </NavSelect>
  );
};
