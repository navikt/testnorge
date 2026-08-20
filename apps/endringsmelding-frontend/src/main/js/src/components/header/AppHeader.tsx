import React, { useEffect, useState } from 'react';
import { BodyShort, Heading } from '@navikt/ds-react';
import styled from 'styled-components';
import navLogo from '@/assets/img/nav-logo-hvit.png';
import ProfilService from '@/service/ProfilService';

const headerMinHeight = 'calc(var(--ax-space-28) + var(--ax-space-40) + var(--ax-space-2))';
const headerSectionPadding = 'calc(var(--ax-space-12) + (var(--ax-space-6) / 2))';
const dividerSpacing = 'calc(var(--ax-space-12) + (var(--ax-space-6) / 2))';

const HeaderBar = styled.header`
  width: 100%;
  min-height: ${headerMinHeight};
  display: flex;
  align-items: stretch;
  background-color: var(--ax-bg-neutral-strong);
  color: var(--ax-text-neutral-contrast);
`;

const LogoSection = styled.div`
  display: flex;
  align-items: center;
  flex-shrink: 0;
  padding-inline: ${headerSectionPadding};

  @media (max-width: 480px) {
    padding-inline: var(--ax-space-8);
  }
`;

const Divider = styled.div`
  width: var(--ax-space-2);
  background-color: var(--ax-border-accent);
  margin-block: ${dividerSpacing};
`;

const ProfileDivider = styled(Divider)`
  @media (max-width: 480px) {
    display: none;
  }
`;

const TitleSection = styled.div`
  display: flex;
  align-items: center;
  flex: 0 1 auto;
  padding-inline: calc(var(--ax-space-8) + var(--ax-space-2));
  min-width: 0;

  @media (max-width: 480px) {
    flex: 1 1 auto;
    padding-inline: var(--ax-space-8);
  }
`;

const Title = styled(Heading)`
  margin: 0;
  color: inherit;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;

  @media (max-width: 480px) {
    font-size: var(--ax-font-size-heading-xsmall);
    line-height: var(--ax-font-line-height-heading-xsmall);
  }
`;

const Spacer = styled.div`
  flex: 1 1 auto;
  min-width: 0;
`;

const ProfileSection = styled.div`
  display: flex;
  align-items: center;
  justify-content: flex-end;
  flex: 0 1 10rem;
  min-width: 0;
  max-width: 10rem;
  padding-inline: ${headerSectionPadding};
  text-align: right;

  @media (max-width: 480px) {
    position: absolute;
    width: 1px;
    height: 1px;
    padding: 0;
    overflow: hidden;
    clip: rect(0, 0, 0, 0);
    white-space: nowrap;
  }
`;

const ProfileText = styled(BodyShort)`
  display: block;
  min-width: 0;
  color: inherit;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
`;

const Logo = styled.img`
  display: block;
  width: auto;
  height: var(--ax-space-40);

  @media (max-width: 480px) {
    height: var(--ax-space-32);
  }
`;

export const AppHeader = () => {
  const [visningsnavn, setVisningsnavn] = useState('Laster profil...');

  useEffect(() => {
    let isMounted = true;

    ProfilService.fetchProfil()
      .then(({ visningsNavn }) => {
        if (!isMounted) {
          return;
        }

        setVisningsnavn(visningsNavn?.trim() || 'Ukjent bruker');
      })
      .catch(() => {
        if (!isMounted) {
          return;
        }

        setVisningsnavn('Profil utilgjengelig');
      });

    return () => {
      isMounted = false;
    };
  }, []);

  return (
    <HeaderBar>
      <LogoSection>
        <Logo src={navLogo} alt="NAV logo" />
      </LogoSection>
      <Divider />
      <TitleSection>
        <Title size="small" level="1">
          Endringsmeldinger
        </Title>
      </TitleSection>
      <Spacer />
      <ProfileDivider />
      <ProfileSection>
        <ProfileText size="small">{visningsnavn}</ProfileText>
      </ProfileSection>
    </HeaderBar>
  );
};
