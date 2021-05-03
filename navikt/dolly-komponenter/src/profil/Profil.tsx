import React from 'react';
import styled from 'styled-components';
// @ts-ignore
import { dollyLogo } from '@navikt/dolly-assets';

type Props = {
  profilbilde?: Response;
  visningsnavn?: string;
};

const Profil = styled.div`
  padding: 20px 10px;
  display: flex;
`;

const VisningsNavn = styled.p`
  height: 30px;
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

export default ({ profilbilde, visningsnavn }: Props) => (
  <Profil>
    <Logo alt="Profilbilde" src={profilbilde ? profilbilde.url : dollyLogo} />
    {visningsnavn && <VisningsNavn>{visningsnavn}</VisningsNavn>}
  </Profil>
);
