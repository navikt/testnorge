import React from 'react';
import styled from 'styled-components';
import { dollyLogo } from '@navikt/dolly-assets/src';

export type ProfilProps = {
  url?: string;
  visningsnavn?: string;
};

const StyledProfil = styled.div`
  padding: 15px 10px;
  display: flex;
`;

const VisningsNavn = styled.p`
  height: 30px;
  min-width: 140px;
  display: flex;
  margin: 0;
  align-self: center;
  color: white;
`;

const Logo = styled.img`
  width: 40px;
  margin: 0 20px;
  height: 40px;
  border-radius: 50%;
  border: 2px solid white;
`;

const Profile = ({ url, visningsnavn }: ProfilProps) => (
  <StyledProfil>
    <Logo alt="Profilbilde" src={url ? url : dollyLogo} />
    {visningsnavn && <VisningsNavn>{visningsnavn}</VisningsNavn>}
  </StyledProfil>
);

export default Profile;
