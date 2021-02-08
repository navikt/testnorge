import React from 'react';

import Select from 'react-select';
import { Label, Select as NavSelect } from 'nav-frontend-skjema';

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
};

export default ({ multi, options, onChange, htmlId, label, ...props }: Props) => {
  if (multi) {
    return (
      <div {...props}>
        <Label htmlFor={htmlId}>{label}</Label>
        <Select
          onChange={(value) => onChange(value.map((item) => item.value))}
          inputId={htmlId}
          isMulti={true}
          options={options}
        />
      </div>
    );
  }

  return (
    <NavSelect
      id={htmlId}
      label={label}
      onChange={(value) => onChange([value.target.value])}
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
