import styled from 'styled-components';
import { Select } from '../../select';
import { DatePicker } from '../../date-picker';
import { TextField } from '@navikt/ds-react';

const InputFormItem: any = styled(TextField)`
  width: 50%;
  padding-right: 10px;
`;

const SelectFormItem: any = styled(Select)`
  width: 25%;
  padding-right: 10px;
`;

const DatePickerFormItem: any = styled(DatePicker)`
  width: 50%;
  padding-right: 10px;
`;

export { InputFormItem, SelectFormItem, DatePickerFormItem };
