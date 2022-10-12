import styled from 'styled-components';
import { Select } from '../../select';
import { DatePicker } from '../../date-picker';
import { TextField } from '@navikt/ds-react';

const InputFormItem = styled(TextField)`
  width: 50%;
  padding-right: 10px;
`;

const SelectFormItem = styled(Select)`
  width: 25%;
  padding-right: 10px;
`;

const DatePickerFormItem = styled(DatePicker)`
  width: 50%;
  padding-right: 10px;
`;

export { InputFormItem, SelectFormItem, DatePickerFormItem };
