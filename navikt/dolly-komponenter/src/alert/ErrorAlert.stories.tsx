import * as React from 'react';
import { Story, Meta } from '@storybook/react-vite';
import ErrorAlert, { ErrorAlertProps } from './ErrorAlert';

export default {
  title: 'Alert/ErrorAlert',
  component: ErrorAlert,
} as Meta;

const Template: Story<ErrorAlertProps> = (args) => <ErrorAlert {...args} />;
export const MiniErrorAlert = Template.bind({});
MiniErrorAlert.args = {
  label: 'Error text',
};
