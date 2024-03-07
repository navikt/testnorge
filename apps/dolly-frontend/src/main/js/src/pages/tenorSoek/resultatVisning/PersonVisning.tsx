import { Alert, Box } from '@navikt/ds-react'
import React from 'react'
import _ from 'lodash'
import { FolkeregisteretVisning } from '@/pages/tenorSoek/resultatVisning/FolkeregisteretVisning'
import styled from 'styled-components'
import { InntektVisning } from '@/pages/tenorSoek/resultatVisning/InntektVisning'
import Loading from '@/components/ui/loading/Loading'
import { EnhetsregisteretForetaksregisteretVisning } from '@/pages/tenorSoek/resultatVisning/EnhetsregisteretForetaksregisteretVisning'
import { NavigerTilPerson } from '@/pages/tenorSoek/resultatVisning/NavigerTilPerson'
import { ImporterPerson } from '@/pages/tenorSoek/resultatVisning/ImporterPerson'
import { ListeValg } from '@/pages/tenorSoek/resultatVisning/ListeValg'

const NavnHeader = styled.h2`
	margin: 10px 0 15px 0;
	word-break: break-word;
	hyphens: auto;
`
export const PersonVisning = ({ person, ident, loading, error }) => {
	if (loading) {
		return <Loading label="Laster person ..." />
	}

	if (error) {
		return <Alert variant="error">{`Feil ved henting av person: ${error}`}</Alert>
	}

	if (!person) {
		return null
	}

	const personData = person.data?.dokumentListe?.[0]

	return (
		<Box background="surface-default" padding="3" borderRadius="medium">
			<div className="flexbox--space">
				<NavnHeader>{personData?.visningnavn}</NavnHeader>
				<ListeValg ident={ident} />
				{/*<NavigerTilPerson ident={ident} />*/}
				{/*<ImporterPerson ident={ident} />*/}
			</div>
			<FolkeregisteretVisning data={personData} />
			<EnhetsregisteretForetaksregisteretVisning
				data={_.get(personData, 'tenorRelasjoner.brreg-er-fr')}
			/>
			<InntektVisning data={personData?.tenorRelasjoner?.inntekt} />
		</Box>
	)
}
