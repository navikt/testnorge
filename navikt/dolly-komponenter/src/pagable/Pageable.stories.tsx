import * as React from 'react';
import { Meta, Story } from '@storybook/react-vite';
import Comp, { PaginationProps } from './Pageable';

export default {
  title: 'Pageable/Pageable',
  component: Comp,
} as Meta;

const Template: Story<PaginationProps<{}>> = (args) => <Comp {...args} />;
export const Pageable = Template.bind({});

const getItems = () => {
  const items = [];

  for (let i = 0; i < 100; i++) {
    items.push(`Testing av paginering #${i}`);
  }
  return items;
};

Pageable.args = {
  items: getItems(),
  render: (items) => (
    <table>
      <tr>
        <th>Text</th>
      </tr>
      <tr>
        {items.map((item) => (
          <p>{item}</p>
        ))}
      </tr>
    </table>
  ),
};
