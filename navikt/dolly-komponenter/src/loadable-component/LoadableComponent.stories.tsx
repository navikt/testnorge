import * as React from 'react';
import { Story, Meta } from '@storybook/react';
import Comp, { LoadableComponentProps } from './LoadableComponent';
import { NotFoundError } from '@navikt/dolly-lib';

export default {
  title: 'LoadableComponent/LoadableComponent',
  component: Comp,
} as Meta;

const Template: Story<LoadableComponentProps<{}>> = (args) => <Comp {...args} />;
export const SuccessLoadableComponent = Template.bind({});
SuccessLoadableComponent.args = {
  onFetch: () =>
    new Promise((resolve) => {
      setTimeout(() => {
        resolve('Success response');
      }, 1000);
    }),
  render: (data) => <h1>{data}</h1>,
};

export const NotFoundLoadableComponent = Template.bind({});
NotFoundLoadableComponent.args = {
  onFetch: () =>
    new Promise((resolve, reject) => {
      setTimeout(() => {
        reject(new NotFoundError());
      }, 1000);
    }),
  render: (data) => <h1>{data}</h1>,
  onNotFound: null,
};

export const ErrorLoadableComponent = Template.bind({});
ErrorLoadableComponent.args = {
  onFetch: () =>
    new Promise((resolve, reject) => {
      setTimeout(() => {
        reject();
      }, 1000);
    }),
  render: (data) => <h1>{data}</h1>,
};
