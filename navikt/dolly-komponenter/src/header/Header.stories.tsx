import * as React from 'react';
import { Meta, Story } from '@storybook/react-vite';
import Comp, { HeaderProps } from './Header';
import Profil from '../profil/Profil';
import { HeaderLinkGroup } from '../header-link-group';
import { HeaderLink } from '../header-link';

export default {
  title: 'Header/Header',
  component: Comp,
} as Meta;

const Template: Story<HeaderProps> = (args) => <Comp {...args} />;
export const Header = Template.bind({});
Header.args = {
  title: 'Header text',
};

export const ProfileHeader = Template.bind({});
ProfileHeader.args = {
  title: 'Header with profile',
  profile: <Profil visningsnavn="Jon Smith" />,
};

const TemplateLinkGroup: Story<HeaderProps> = (args) => (
  <Comp {...args}>
    <HeaderLinkGroup>
      <HeaderLink href="#/link1" isActive={() => true}>
        Link 1
      </HeaderLink>
      <HeaderLink href="#/link2" isActive={() => false}>
        Link 2
      </HeaderLink>
      <HeaderLink href="#/link3" isActive={() => false}>
        Link 3
      </HeaderLink>
    </HeaderLinkGroup>
  </Comp>
);

export const LinksHeader = TemplateLinkGroup.bind({});
LinksHeader.args = {
  title: 'Header with links',
};

export const LinksWithProfileHeader = TemplateLinkGroup.bind({});
LinksWithProfileHeader.args = {
  title: 'Header with links and profile',
  profile: <Profil visningsnavn="Jon Smith" />,
};
