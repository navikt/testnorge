import { Alert, Box } from '@navikt/ds-react'
import React from 'react'
import * as _ from 'lodash-es'
import { FolkeregisteretVisning } from '@/pages/tenorSoek/resultatVisning/FolkeregisteretVisning'
import styled from 'styled-components'
import { InntektVisning } from '@/pages/tenorSoek/resultatVisning/InntektVisning'
import Loading from '@/components/ui/loading/Loading'
import { EnhetsregisteretForetaksregisteretVisning } from '@/pages/tenorSoek/resultatVisning/EnhetsregisteretForetaksregisteretVisning'

const NavnHeader = styled.h2`
	margin: 5px 0 15px 0;
`
export const PersonVisning = ({ person, loading, error }) => {
	// console.log('person: ', person) //TODO - SLETT MEG
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
	// TODO maa kanskje ta med flere personer hvis lista er lengre?

	return (
		<Box background="surface-default" padding="3">
			<NavnHeader>{personData?.visningnavn}</NavnHeader>
			<FolkeregisteretVisning data={personData} />
			<EnhetsregisteretForetaksregisteretVisning
				data={_.get(personData, 'tenorRelasjoner.brreg-er-fr')}
			/>
			<InntektVisning data={personData?.tenorRelasjoner?.inntekt} />
		</Box>
	)
}
