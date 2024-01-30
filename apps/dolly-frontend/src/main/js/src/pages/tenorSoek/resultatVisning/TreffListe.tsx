import { Box, VStack, Tag } from '@navikt/ds-react'
import React, { useEffect, useState } from 'react'
import { useTenorIdent } from '@/utils/hooks/useTenorSoek'
import { PersonVisning } from '@/pages/tenorSoek/resultatVisning/PersonVisning'
export const TreffListe = ({ response }: any) => {
	if (!response) {
		return null
	}
	console.log('response: ', response?.data?.personer) //TODO - SLETT MEG

	const [valgtPerson, setValgtPerson] = useState(null)
	const {
		person: valgtPersonData,
		loading: valgtPersonLoading,
		error: valgtPersonError,
	} = useTenorIdent(valgtPerson?.identifikator)

	// console.log('valgtPerson: ', valgtPerson) //TODO - SLETT MEG
	// console.log('person: ', valgtPersonData) //TODO - SLETT MEG

	useEffect(() => {
		setValgtPerson(response?.data?.personer?.[0] || null)
	}, [response])

	const antallTreff = response?.data?.treff
	return (
		<div className="flexbox--flex-wrap">
			<div style={{ width: '30%' }}>
				<h2>{antallTreff} treff</h2>
				{/*<p>{response}</p>*/}
				<VStack gap="4">
					{response?.data?.personer?.map((person: any) => (
						// <Box key={person.identifikator} padding="2" border="1" borderColor="navds-color-gray-20">
						<Box
							key={person.identifikator}
							padding="2"
							background={
								person.identifikator === valgtPerson?.identifikator
									? 'surface-alt-3-moderate'
									: 'surface-alt-3-subtle'
							}
							// background={'surface-alt-3-subtle'}
							onClick={() => setValgtPerson(person)}
						>
							{/*<h3>{person.visningnavn}</h3>*/}
							<h3>
								{person.fornavn} {person.etternavn}
							</h3>
							<p>{person.identifikator}</p>
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
					// <div className="dolly-panel-content">

					// </div>
				)}
			</div>
		</div>
	)
}
