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
export const TreffListe = ({ response, loading, error }: any) => {
	if (loading) {
		return <Loading label="Laster treff ..." />
	}

	if (error || response?.error) {
		return (
			<Alert variant="error" size="small">{`Feil ved henting av personer: ${
				error || response?.error
			}`}</Alert>
		)
	}

	if (!response) {
		return null
	}

	const [valgtPerson, setValgtPerson] = useState(null)

	const {
		person: valgtPersonData,
		loading: valgtPersonLoading,
		error: valgtPersonError,
	} = useTenorIdent(valgtPerson?.identifikator)

	useEffect(() => {
		setValgtPerson(response?.data?.personer?.[0] || null)
	}, [response])

	const antallTreff = response?.data?.treff

	return (
		<div className="flexbox--flex-wrap">
			<div style={{ width: '30%' }}>
				<h2>{antallTreff} treff</h2>
				<VStack gap="4">
					{response?.data?.personer?.map((person: any) => (
						<Box
							key={person.identifikator}
							padding="2"
							background={
								person.identifikator === valgtPerson?.identifikator
									? 'surface-alt-3-moderate'
									: 'surface-alt-3-subtle'
							}
							borderRadius="medium"
							onClick={() => setValgtPerson(person)}
							style={{ cursor: 'pointer' }}
						>
							<PersonNavn>
								{person.fornavn} {person.etternavn}
							</PersonNavn>
							<PersonIdent>{person.identifikator}</PersonIdent>
							{person.tenorRelasjoner?.map((relasjon: any, idx: number) => (
								<Tag
									size="small"
									variant="neutral"
									key={person.identifikator + idx}
									style={{ margin: '0 5px 5px 0' }}
								>
									{relasjon}
								</Tag>
							))}
						</Box>
					))}
				</VStack>
			</div>
			<div style={{ width: '68%', marginLeft: '2%', marginTop: '68px' }}>
				{valgtPerson && (
					<PersonVisning
						person={valgtPersonData?.data}
						loading={valgtPersonLoading}
						error={valgtPersonError}
					/>
				)}
			</div>
		</div>
	)
}
