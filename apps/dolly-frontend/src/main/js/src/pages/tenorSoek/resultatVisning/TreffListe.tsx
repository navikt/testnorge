import { Box, VStack, Tag } from '@navikt/ds-react'
import { Simulate } from 'react-dom/test-utils'
import click = Simulate.click
import React, { useState } from 'react'
import { PersonVisning } from '@/pages/dollySoek/PersonVisning'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
export const TreffListe = ({ response }: any) => {
	if (!response) {
		return null
	}
	console.log('response: ', response) //TODO - SLETT MEG

	const [valgtPerson, setValgtPerson] = useState(null)
	console.log('valgtPerson: ', valgtPerson) //TODO - SLETT MEG
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
					// <div className="dolly-panel-content">
					<Box background="surface-default" padding="3">
						<SubOverskrift label="Persondetaljer" iconKind="personinformasjon" />
						<div className="person-visning_content">
							<TitleValue title="Navn" value={valgtPerson.visningnavn} />
							<TitleValue title="Ident" value={valgtPerson.identifikator} />
							<TitleValue title="Fødselsdato" value={valgtPerson.foedselsdato} />
							<TitleValue title="Kjønn" value={valgtPerson.kjoenn} />
							<TitleValue title="Personstatus" value={valgtPerson.personstatus} />
							<TitleValue title="Sivilstand" value={valgtPerson.sivilstand} />
							<TitleValue title="Bostedsadresse" value={valgtPerson.bostedsadresse} />
							<TitleValue title="Siste hendelse" value={valgtPerson.sisteHendelse} />
						</div>
					</Box>
					// </div>
				)}
			</div>
		</div>
	)
}
