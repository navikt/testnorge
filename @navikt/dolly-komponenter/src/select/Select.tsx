import React from 'react';

import Select from 'react-select';
import { Label, Select as NavSelect } from 'nav-frontend-skjema';

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

export default ({ multi, options, onChange, htmlId, label, error, ...props }: Props) => {
  if (multi) {
    return (
      <div {...props}>
        <Label htmlFor={htmlId}>{label}</Label>

        <Select
          className={error ? 'skjemaelement__input--harFeil select--error' : 'select'}
          onChange={(value) => onChange(value.map((item) => item.value))}
          inputId={htmlId}
          isMulti={true}
          options={options}
        />
        {error && (
          <div role="alert" aria-live="assertive" className="skjemaelement__feilmelding">
            <p className="typo-feilmelding">{error}</p>
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
      feil={error}
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
