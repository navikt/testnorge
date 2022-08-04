import styled from 'styled-components';
import { Select } from '../../select';
import { DatePicker } from '../../date-picker';
import { TextField } from '@navikt/ds-react';

const InputFormItem = styled(TextField)`
  padding-right: 10px;
`;

const SelectFormItem = styled(Select)`
  padding-right: 10px;
`;

const DatePickerFormItem = styled(DatePicker)`
  padding-right: 10px;
`;

export { InputFormItem, SelectFormItem, DatePickerFormItem };
