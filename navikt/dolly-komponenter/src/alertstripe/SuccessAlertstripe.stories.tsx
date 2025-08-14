import * as React from 'react';
import { Story, Meta } from '@storybook/react-vite';
import Comp, { SuccessAlertstripeProps } from './SuccessAlertstripe';

export default {
  title: 'Alertstripe/SuccessAlertstripe',
  component: Comp,
} as Meta;

const Template: Story<SuccessAlertstripeProps> = (args) => <Comp {...args} />;
export const SuccessAlertstripe = Template.bind({});
SuccessAlertstripe.args = {
  label: 'Success text',
};
