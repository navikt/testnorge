import { Alert, Box, Tag, VStack } from '@navikt/ds-react'
import React, { useEffect, useState } from 'react'
import { useTenorOrganisasjon } from '@/utils/hooks/useTenorSoek'
import Loading from '@/components/ui/loading/Loading'
import styled from 'styled-components'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { OrganisasjonTenorVisning } from '@/pages/organisasjoner/OrganisasjonTenorSoek/resultatVisning/OrganisasjonTenorVisning'

const OrganisasjonNavn = styled.h3`
	word-break: break-word;
	hyphens: auto;
	margin: 5px 0;
`

const OrgInfo = styled.p`
	margin: 5px 0 0 0;
`

const TagsWrapper = styled.div`
	margin: 10px 0;
`

export const TreffListeOrg = ({ response, organisasjonListe, loading, error }: any) => {
	const [valgtOrganisasjon, setValgtOrganisasjon] = useState<any>(null)

	const {
		organisasjon: valgtOrganisasjonData,
		loading: valgtOrganisasjonLoading,
		error: valgtOrganisasjonError,
	} = useTenorOrganisasjon(valgtOrganisasjon?.organisasjonsnummer)

	useEffect(() => {
		setValgtOrganisasjon(organisasjonListe?.[0] || null)
	}, [organisasjonListe?.[0]])

	if ((!organisasjonListe || organisasjonListe?.length === 0) && loading) {
		return <Loading label="Laster treff ..." />
	}

	if (error || response?.error) {
		return (
			<Alert
				variant="error"
				style={{ marginTop: '-70px' }}
				size="small"
			>{`Feil ved henting av organisasjoner: ${error || response?.error}`}</Alert>
		)
	}

	const antallTreff = response?.data?.treff

	return (
		<div className="flexbox--flex-wrap" style={{ marginTop: '-70px' }}>
			<div
				className="flexbox--full-width"
				style={{ marginBottom: '20px', position: 'sticky', top: '10px', zIndex: 1 }}
			>
				<Box background="default" padding="space-12" borderRadius="4">
					<div className="flexbox--space">
						<h2 style={{ margin: '0', alignSelf: 'center' }}>
							{antallTreff || antallTreff === 0 ? `${antallTreff} treff` : ''}
						</h2>
					</div>
				</Box>
			</div>
			<div style={{ width: '30%' }}>
				<VStack gap="space-16">
					{organisasjonListe?.map((organisasjon: any) => {
						return (
							<Box
								key={organisasjon?.organisasjonsnummer}
								data-testid={TestComponentSelectors.BUTTON_ORGANISASJON_TENORSOEK}
								padding="space-8"
								background={
									organisasjon?.organisasjonsnummer === valgtOrganisasjon?.organisasjonsnummer
										? 'accent-moderate-pressed'
										: 'accent-moderate'
								}
								borderColor={
									organisasjon?.organisasjonsnummer === valgtOrganisasjon?.organisasjonsnummer
										? 'accent-strong'
										: 'accent'
								}
								borderWidth="1"
								borderRadius="4"
								onClick={() => setValgtOrganisasjon(organisasjon)}
								style={{ cursor: 'pointer' }}
							>
								<OrganisasjonNavn>{organisasjon?.navn}</OrganisasjonNavn>
								<OrgInfo>{organisasjon?.organisasjonsnummer}</OrgInfo>
								<TagsWrapper>
									{organisasjon?.kilder?.map((relasjon: any, idx: number) => (
										<Tag
											data-color="brand-beige"
											size="small"
											variant="outline"
											key={organisasjon?.organisasjonsnummer + idx}
											style={{ margin: '0 5px 5px 0' }}
										>
											{relasjon.includes('tjenestepensjon') ? 'T.P. Opplysningspliktig' : relasjon}
										</Tag>
									))}
								</TagsWrapper>
							</Box>
						)
					})}
				</VStack>
				{loading && (
					<div style={{ margin: '10px 0' }}>
						<Loading label="Laster treff ..." />
					</div>
				)}
				{organisasjonListe?.length === 200 && antallTreff > 200 && (
					<Alert variant="info" size="small" inline style={{ marginTop: '20px' }}>
						Viser kun de 200 første treffene. <br /> Gjør et nytt søk for å se andre treff.
					</Alert>
				)}
			</div>
			<div
				style={{
					width: '68%',
					marginLeft: '2%',
				}}
			>
				{valgtOrganisasjon && (
					<OrganisasjonTenorVisning
						organisasjon={valgtOrganisasjonData?.data}
						orgnummer={valgtOrganisasjon?.organisasjonsnummer}
						loading={valgtOrganisasjonLoading}
						error={valgtOrganisasjonError}
					/>
				)}
			</div>
		</div>
	)
}
