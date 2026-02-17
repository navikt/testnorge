import { Alert, Box, Tag, Tooltip, VStack } from '@navikt/ds-react'
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

const getTagTooltip = (relasjon: string): string => {
	const tooltips: Record<string, string> = {
		Arbeidsforhold: 'Personen har arbeidsforhold i Aareg',
		BeregnetSkatt: 'Personen har beregnet skatt',
		BrregErFr:
			'Personen har rolle(r) i Brønnøysundregistrene (Enhetsregisteret og Foretaksregisteret)',
		Freg: 'Personen har data i Folkeregisteret',
		Inntekt: 'Personen har inntekt i A-ordningen',
		TestinnsendingSkattPerson: 'Personen har testinnsending skatt (person)',
		SamletReskontroinnsyn: 'Personen har samlet reskontroinnsyn',
		Skattemelding: 'Personen har skattemelding',
		Skatteplikt: 'Personen har skatteplikt',
		SpesifisertSummertSkattegrunnlag: 'Personen har spesifisert summert skattegrunnlag',
		SummertSkattegrunnlag: 'Personen har summert skattegrunnlag',
		Tilleggsskatt: 'Personen har tilleggsskatt',
		Tjenestepensjonsavtale: 'Personen har tjenestepensjonsavtale',
	}
	return tooltips[relasjon] || relasjon
}

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
				<Box background="default" padding="space-12" borderRadius="4">
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
				<VStack gap="space-16">
					{personListe?.map((person: any) => {
						return (
							<Box
								key={person?.id}
								data-testid={TestComponentSelectors.BUTTON_PERSON_TENORSOEK}
								padding="space-8"
								background={
									person?.id === valgtPerson?.id ? 'accent-moderate-pressed' : 'accent-moderate'
								}
								borderColor={person?.id === valgtPerson?.id ? 'accent-strong' : 'accent'}
								borderWidth="1"
								borderRadius="4"
								onClick={() => setValgtPerson(person)}
								style={{ cursor: 'pointer' }}
							>
								<PersonNavn>
									{person?.fornavn} {person?.etternavn}
								</PersonNavn>
								<PersonIdent>{person?.id}</PersonIdent>
								<TagsWrapper>
									{person?.iarenaSynt && (
										<Tooltip content="Person har allerede arbeidsytelse(r) i Arena">
											<Tag
												data-color="meta-purple"
												size="small"
												variant="outline"
												key={person?.id + 'arena'}
												style={{ margin: '0 5px 5px 0' }}
											>
												Arena
											</Tag>
										</Tooltip>
									)}
									{person?.tenorRelasjoner?.map((relasjon: any, idx: number) => (
										<Tooltip content={getTagTooltip(relasjon)} key={person?.id + idx}>
											<Tag
												data-color="neutral"
												size="small"
												variant="outline"
												style={{ margin: '0 5px 5px 0' }}
											>
												{relasjon}
											</Tag>
										</Tooltip>
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
