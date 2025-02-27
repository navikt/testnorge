import { Alert, Box, Tag, VStack } from '@navikt/ds-react'
import React, { useEffect, useState } from 'react'
import { useTenorIdent } from '@/utils/hooks/useTenorSoek'
import { PersonVisning } from '@/pages/tenorSoek/resultatVisning/PersonVisning'
import Loading from '@/components/ui/loading/Loading'
import styled from 'styled-components'
import { ListeValg } from '@/pages/tenorSoek/resultatVisning/ListeValg'
import { ImporterValgtePersoner } from '@/pages/tenorSoek/resultatVisning/ImporterValgtePersoner'
import { TestComponentSelectors } from '#/mocks/Selectors'

const PersonNavn = styled.h3`
	word-break: break-word;
	hyphens: auto;
	margin: 5px 0;
`

const PersonIdent = styled.p`
	margin: 5px 0 0 0;
`

const TagsWrapper = styled.div`
	margin: 10px 0;
`

export const TreffListe = ({
	response,
	personListe,
	markertePersoner,
	setMarkertePersoner,
	inkluderPartnere,
	setInkluderPartnere,
	nesteSide,
	loading,
	error,
}: any) => {
	const [valgtPerson, setValgtPerson] = useState<any>(null)

	const {
		person: valgtPersonData,
		loading: valgtPersonLoading,
		error: valgtPersonError,
	} = useTenorIdent(valgtPerson?.id)

	useEffect(() => {
		setValgtPerson(personListe?.[0] || null)
	}, [personListe?.[0]])

	useEffect(() => {
		if (response?.data?.treff || response?.data?.treff === 0) {
			localStorage['antallTreff'] = response?.data?.treff
		}
	}, [response])

	if ((!personListe || personListe?.length === 0) && loading) {
		return (
			<div style={{ marginTop: '-70px' }}>
				<Loading label="Laster treff ..." />
			</div>
		)
	}

	if (error || response?.error) {
		return (
			<Alert
				variant="error"
				size="small"
				style={{ marginTop: '-70px' }}
			>{`Feil ved henting av personer: ${error || response?.error}`}</Alert>
		)
	}

	const antallTreff = localStorage['antallTreff']

	return (
		<div className="flexbox--flex-wrap" style={{ marginTop: '-70px' }}>
			<div
				className="flexbox--full-width"
				style={{ marginBottom: '20px', position: 'sticky', top: '10px', zIndex: 1 }}
			>
				<Box background="surface-default" padding="3" borderRadius="medium">
					<div className="flexbox--space">
						<h2 style={{ margin: '0', alignSelf: 'center' }}>
							{antallTreff ? `${antallTreff} treff` : ''}
						</h2>
						<ImporterValgtePersoner
							identer={markertePersoner}
							isMultiple={true}
							inkluderPartnere={inkluderPartnere}
							setInkluderPartnere={setInkluderPartnere}
						/>
					</div>
				</Box>
			</div>
			<div style={{ width: '30%' }}>
				<VStack gap="4">
					{personListe?.map((person: any) => {
						return (
							<Box
								key={person?.id}
								data-testid={TestComponentSelectors.BUTTON_PERSON_TENORSOEK}
								padding="2"
								background={
									person?.id === valgtPerson?.id ? 'surface-alt-3-moderate' : 'surface-alt-3-subtle'
								}
								borderRadius="medium"
								onClick={() => setValgtPerson(person)}
								style={{ cursor: 'pointer' }}
							>
								<PersonNavn>
									{person?.fornavn} {person?.etternavn}
								</PersonNavn>
								<PersonIdent>{person?.id}</PersonIdent>
								<TagsWrapper>
									{person?.tenorRelasjoner?.map((relasjon: any, idx: number) => (
										<Tag
											size="small"
											variant="neutral"
											key={person?.id + idx}
											style={{ margin: '0 5px 5px 0' }}
										>
											{relasjon}
										</Tag>
									))}
								</TagsWrapper>
								<ListeValg
									person={person}
									markertePersoner={markertePersoner}
									setMarkertePersoner={setMarkertePersoner}
								/>
							</Box>
						)
					})}
				</VStack>
				{loading && (
					<div style={{ margin: '10px 0' }}>
						<Loading label="Laster treff ..." />
					</div>
				)}
				{!nesteSide && antallTreff > 200 && (
					<Alert variant="info" size="small" inline style={{ marginTop: '20px' }}>
						Viser kun de {personListe?.length} første treffene. <br /> Gjør et nytt søk for å se
						andre treff.
					</Alert>
				)}
			</div>
			<div
				style={{
					width: '68%',
					marginLeft: '2%',
				}}
			>
				{valgtPerson && (
					<PersonVisning
						person={valgtPersonData?.data}
						ident={valgtPerson?.id}
						ibruk={valgtPerson?.ibruk}
						loading={valgtPersonLoading}
						error={valgtPersonError}
						inkluderPartnere={inkluderPartnere}
						setInkluderPartnere={setInkluderPartnere}
					/>
				)}
			</div>
		</div>
	)
}
