import styled from 'styled-components';
import { Input } from 'nav-frontend-skjema';
import { Select } from '../../select';
import { DatePicker } from '../../date-picker';

const InputFormItem = styled(Input)`
  width: 50%;
  padding-right: 10px;
`;

const SelectFormItem = styled(Select)`
  width: 25%;
  padding-right: 10px;
`;

const DatePickerFormItem = styled(DatePicker)`
  width: 25%;
  padding-right: 10px;
`;

export { InputFormItem, SelectFormItem, DatePickerFormItem };
