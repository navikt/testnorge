import * as React from 'react';
import { Story, Meta } from '@storybook/react-vite';
import Comp, { WarningAlertProps } from './WarningAlert';

export default {
  title: 'Alert/WarningAlert',
  component: Comp,
} as Meta;

const Template: Story<WarningAlertProps> = (args) => <Comp {...args} />;
export const WarningAlert = Template.bind({});
WarningAlert.args = {
  label: 'Warning text',
};
