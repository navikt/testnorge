import * as React from 'react';
import { Story, Meta } from '@storybook/react';
import Comp, { SuccessAlertProps } from './SuccessAlert';

export default {
  title: 'Alert/SuccessAlert',
  component: Comp,
} as Meta;

const Template: Story<SuccessAlertProps> = (args) => <Comp {...args} />;
export const SuccessAlert = Template.bind({});
SuccessAlert.args = {
  label: 'Success text',
};
