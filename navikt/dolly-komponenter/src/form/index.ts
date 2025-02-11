import {DatePickerFormItem, InputFormItem, SelectFormItem} from './item';
import styled from 'styled-components';

const Form = styled.form`
    display: flex;
    flex-direction: column;
    align-content: space-around;
    justify-content: space-between;
`;

const Line = styled.div<{ reverse: boolean }>`
    padding-top: 30px;
    display: flex;
    flex-direction: ${(props) => (props.reverse ? 'row-reverse' : 'row')};
`;

export {Line, Form, InputFormItem, DatePickerFormItem, SelectFormItem};
