import { Box, VStack, Tag, Alert } from '@navikt/ds-react'
import React, { useEffect, useState } from 'react'
import { useTenorIdent } from '@/utils/hooks/useTenorSoek'
import { PersonVisning } from '@/pages/tenorSoek/resultatVisning/PersonVisning'
import Loading from '@/components/ui/loading/Loading'
import styled from 'styled-components'

const PersonNavn = styled.h3`
	word-break: break-word;
	hyphens: auto;
	margin: 5px 0;
`

const PersonIdent = styled.p`
	margin: 5px 0 15px 0;
`

const PersonVisningWrapper = styled.div`
	position: sticky;
	top: 10px;
	max-height: 96vh;
	overflow: auto;
	scrollbar-width: none;
`

export const TreffListe = ({ response, personListe, loading, error }: any) => {
	if ((!personListe || personListe?.length === 0) && loading) {
		return <Loading label="Laster treff ..." />
	}

	if (error || response?.error) {
		return (
			<Alert variant="error" size="small">{`Feil ved henting av personer: ${
				error || response?.error
			}`}</Alert>
		)
	}

	if (!personListe || personListe?.length === 0) {
		return null
	}

	const [valgtPerson, setValgtPerson] = useState(null)

	const {
		person: valgtPersonData,
		loading: valgtPersonLoading,
		error: valgtPersonError,
	} = useTenorIdent(valgtPerson?.identifikator?.[0])
	//TODO endres tilbake når vi faar ident som ikke er array

	useEffect(() => {
		if (!valgtPerson) {
			setValgtPerson(personListe?.[0] || null)
		}
	}, [personListe?.[0]])

	const antallTreff = response?.data?.treff

	return (
		<div className="flexbox--flex-wrap">
			<div className="flexbox--full-width">
				{antallTreff && <h2 style={{ marginTop: '5px' }}>{antallTreff} treff</h2>}
			</div>
			<div style={{ width: '30%' }}>
				<VStack gap="4">
					{personListe?.map((person: any) => (
						<Box
							key={person?.identifikator}
							padding="2"
							background={
								person?.identifikator === valgtPerson?.identifikator
									? 'surface-alt-3-moderate'
									: 'surface-alt-3-subtle'
							}
							borderRadius="medium"
							onClick={() => setValgtPerson(person)}
							style={{ cursor: 'pointer' }}
						>
							<PersonNavn>
								{person?.fornavn} {person?.etternavn}
							</PersonNavn>
							<PersonIdent>{person?.identifikator}</PersonIdent>
							{person?.tenorRelasjoner?.map((relasjon: any, idx: number) => (
								<Tag
									size="small"
									variant="neutral"
									key={person?.identifikator + idx}
									style={{ margin: '0 5px 5px 0' }}
								>
									{relasjon}
								</Tag>
							))}
						</Box>
					))}
				</VStack>
				{loading && <Loading label="Laster treff ..." />}
			</div>
			<div
				style={{
					width: '68%',
					marginLeft: '2%',
				}}
			>
				{valgtPerson && (
					<PersonVisningWrapper>
						<PersonVisning
							person={valgtPersonData?.data}
							loading={valgtPersonLoading}
							error={valgtPersonError}
						/>
					</PersonVisningWrapper>
				)}
			</div>
		</div>
	)
}
