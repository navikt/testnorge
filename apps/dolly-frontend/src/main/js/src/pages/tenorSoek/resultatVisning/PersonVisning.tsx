import { Alert, Box } from '@navikt/ds-react'
import React from 'react'
import _ from 'lodash'
import { FolkeregisteretVisning } from '@/pages/tenorSoek/resultatVisning/FolkeregisteretVisning'
import styled from 'styled-components'
import { InntektVisning } from '@/pages/tenorSoek/resultatVisning/InntektVisning'
import Loading from '@/components/ui/loading/Loading'
import { EnhetsregisteretForetaksregisteretVisning } from '@/pages/tenorSoek/resultatVisning/EnhetsregisteretForetaksregisteretVisning'
import { NavigerTilPerson } from '@/pages/tenorSoek/resultatVisning/NavigerTilPerson'
import { ImporterValgtePersoner } from '@/pages/tenorSoek/resultatVisning/ImporterValgtePersoner'
import { useFinnesIDolly } from '@/utils/hooks/useIdent'

type PersonVisningProps = {
	person: any
	ident: string
	loading: boolean
	error: any
}

const PersonVisningWrapper = styled.div`
	position: sticky;
	top: 80px;
	max-height: 92vh;
	overflow: auto;
	scrollbar-width: thin;
`

const NavnHeader = styled.h2`
	margin: 10px 0 15px 0;
	word-break: break-word;
	hyphens: auto;
`
export const PersonVisning = ({ person, ident, loading, error }: PersonVisningProps) => {
	const { finnesIDolly, loading: loadingFinnes } = useFinnesIDolly(ident)

	if (loading) {
		return <Loading label="Laster person ..." />
	}

	if (error) {
		return <Alert variant="error">{`Feil ved henting av person: ${error}`}</Alert>
	}

	if (!person) {
		return null
	}

	const personData = person.data?.dokumentListe?.find((dokument: any) =>
		dokument.identifikator?.includes(ident),
	)

	return (
		<PersonVisningWrapper>
			<Box background="surface-default" padding="3" borderRadius="medium">
				<div className="flexbox--space">
					<NavnHeader>{personData?.visningnavn}</NavnHeader>
					{loadingFinnes && <Loading onlySpinner />}
					{!loadingFinnes &&
						(finnesIDolly ? (
							<NavigerTilPerson ident={ident} />
						) : (
							<ImporterValgtePersoner identer={[ident]} isMultiple={false} />
						))}
				</div>
				<FolkeregisteretVisning data={personData} />
				<EnhetsregisteretForetaksregisteretVisning
					data={_.get(personData, 'tenorRelasjoner.brreg-er-fr')}
				/>
				{/*TODO: Vis denne naar det er mulig aa importere og vise inntekt i Dolly*/}
				{/*<InntektVisning data={personData?.tenorRelasjoner?.inntekt} />*/}
			</Box>
		</PersonVisningWrapper>
	)
}
