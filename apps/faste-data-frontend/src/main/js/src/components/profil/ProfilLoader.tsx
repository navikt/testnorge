import React, { useEffect, useState } from 'react';
import { BodyShort } from '@navikt/ds-react';
import styled from 'styled-components';
import ProfilService from '@/service/ProfilService';

const Profile = styled.div`
  display: flex;
  align-items: center;
  gap: var(--ax-space-12);
  min-height: 4.375rem;
  padding-inline: var(--ax-space-20);
  color: var(--ax-text-neutral);

  @media (max-width: 767px) {
    min-height: 3.5rem;
    gap: var(--ax-space-8);
    padding-inline: var(--ax-space-12);
  }
`;

const ProfileImage = styled.div`
  display: grid;
  place-items: center;
  width: 2.5rem;
  height: 2.5rem;
  border: 2px solid currentColor;
  border-radius: 999rem;
  overflow: hidden;
  flex-shrink: 0;
  font-weight: 600;

  @media (max-width: 767px) {
    width: 2rem;
    height: 2rem;
  }
`;

const ProfilePhoto = styled.img`
  width: 100%;
  height: 100%;
  object-fit: cover;
`;

const ProfileName = styled(BodyShort)`
  color: inherit;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 12rem;

  @media (max-width: 767px) {
    max-width: 7rem;
  }
`;

export default () => {
  const [navn, setNavn] = useState<string>();
  const [url, setUrl] = useState<string>();
  const [error, setError] = useState(false);

  useEffect(() => {
    let active = true;

    Promise.allSettled([ProfilService.fetchProfil(), ProfilService.fetchBilde()])
      .then((responses) => {
        if (!active) {
          return;
        }

        const [profileResponse, imageResponse] = responses;

        if (profileResponse.status === 'fulfilled') {
          setNavn(profileResponse.value.visningsNavn?.trim() || 'Ukjent bruker');
        } else {
          setError(true);
        }

        if (imageResponse.status === 'fulfilled' && imageResponse.value?.url) {
          setUrl(imageResponse.value.url);
        }
      })
      .catch(() => {
        if (!active) {
          return;
        }
        setError(true);
      });

    return () => {
      active = false;
    };
  }, []);

  const name = error ? 'Profil utilgjengelig' : navn ?? 'Laster profil...';
  const initial = name.trim().charAt(0).toUpperCase();

  return (
    <Profile aria-label={name}>
      <ProfileImage aria-hidden="true">
        {url ? <ProfilePhoto alt="" src={url} /> : initial}
      </ProfileImage>
      <ProfileName forwardedAs="span" size="small">
        {name}
      </ProfileName>
    </Profile>
  );
};
