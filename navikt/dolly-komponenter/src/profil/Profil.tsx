import React from 'react';
import styled from 'styled-components';
// @ts-ignore
import { dollyLogo } from '@navikt/dolly-assets';

export type ProfilProps = {
  url?: string;
  visningsnavn?: string;
};

const ProfilStyle = styled.div`
  padding: 20px 10px;
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
  width: 28px;
  margin: 0 20px;
  height: 28px;
  border-radius: 50%;
  border: 2px solid white;
`;

const Profile = ({ url, visningsnavn }: ProfilProps) => (
  <ProfilStyle>
    <Logo alt="Profilbilde" src={url ? url : dollyLogo} />
    {visningsnavn && <VisningsNavn>{visningsnavn}</VisningsNavn>}
  </ProfilStyle>
);

export default Profile;
