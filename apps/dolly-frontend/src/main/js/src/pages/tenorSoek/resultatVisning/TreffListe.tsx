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

	const [valgtPerson, setValgtPerson] = useState(null)

	const {
		person: valgtPersonData,
		loading: valgtPersonLoading,
		error: valgtPersonError,
	} = useTenorIdent(valgtPerson?.id)

	useEffect(() => {
		if (!valgtPerson) {
			setValgtPerson(personListe?.[0] || null)
		}
	}, [personListe?.[0]])

	const antallTreff = response?.data?.treff

	return (
		<div className="flexbox--flex-wrap">
			<div className="flexbox--full-width">
				<h2 style={{ marginTop: '5px' }}>
					{antallTreff || antallTreff === 0 ? `${antallTreff} treff` : ''}
				</h2>
			</div>
			<div style={{ width: '30%' }}>
				<VStack gap="4">
					{personListe?.map((person: any) => (
						<Box
							key={person?.id}
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
						</Box>
					))}
				</VStack>
				{loading && (
					<div style={{ margin: '10px 0' }}>
						<Loading label="Laster treff ..." />
					</div>
				)}
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
