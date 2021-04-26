import React from 'react';
import styled from 'styled-components';
// @ts-ignore
import dollyLogo from '../../assets/dolly.svg';

type Props = {
  profilbilde?: Response;
  visningsnavn?: string;
};

const Profil = styled.div`
  height: 30px;
  display: flex;
`;

const VisningsNavn = styled.p`
  margin: 0;
  align-self: center;
  color: white;
`;

const Logo = styled.img`
  width: 30px;
  height: 30px;
  border-radius: 50%;
  border: 2px solid white;
  margin-right: 7px;
`;

export default ({ profilbilde, visningsnavn }: Props) => (
  <Profil>
    <Logo alt="Profilbilde" src={profilbilde ? profilbilde.url : dollyLogo} />
    {visningsnavn && <VisningsNavn>{visningsnavn}</VisningsNavn>}
  </Profil>
);
