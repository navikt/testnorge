import React from 'react';
import { Select as NavSelect } from 'nav-frontend-skjema';

import './Select.less';

type Option<T> = {
  value: T;
  label: string;
};

type Props<T> = {
  htmlId: string;
  multi?: boolean;
  options: Option<T>[];
  onChange: (value: T) => void;
  label: string;
  className?: string;
  error?: string;
};

function Select<T extends string>({
  multi,
  options,
  onChange,
  htmlId,
  label,
  error,
  ...props
}: Props<T>) {
  return (
    <NavSelect
      id={htmlId}
      label={label}
      // @ts-ignore
      onChange={(value) => onChange(value.target.value)}
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
}

export default Select;
