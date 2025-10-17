import { Alert, Box } from '@navikt/ds-react'
import React from 'react'
import * as _ from 'lodash-es'
import { FolkeregisteretVisning } from '@/pages/tenorSoek/resultatVisning/FolkeregisteretVisning'
import styled from 'styled-components'
import { InntektVisning } from '@/pages/tenorSoek/resultatVisning/InntektVisning'
import Loading from '@/components/ui/loading/Loading'
import { EnhetsregisteretForetaksregisteretVisning } from '@/pages/tenorSoek/resultatVisning/EnhetsregisteretForetaksregisteretVisning'
import { NavigerTilPerson } from '@/pages/tenorSoek/resultatVisning/NavigerTilPerson'
import { ImporterValgtePersoner } from '@/pages/tenorSoek/resultatVisning/ImporterValgtePersoner'
import { TjenestepensjonsavtaleVisning } from '@/pages/tenorSoek/resultatVisning/TjenestepensjonsavtaleVisning'
import { SkattemeldingVisning } from '@/pages/tenorSoek/resultatVisning/SkattemeldingVisning'
import { OrganisasjonArbeidsforholdVisning } from '@/pages/organisasjoner/OrganisasjonTenorSoek/resultatVisning/OrganisasjonArbeidsforholdVisning'

type PersonVisningProps = {
	person: any
	ident: string
	ibruk: boolean
	loading: boolean
	error: any
	inkluderPartnere: boolean
	setInkluderPartnere: any
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

export const PersonVisning = ({
	person,
	ident,
	ibruk,
	loading,
	error,
	inkluderPartnere,
	setInkluderPartnere,
}: PersonVisningProps) => {
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
					{ibruk ? (
						<NavigerTilPerson ident={ident} />
					) : (
						<ImporterValgtePersoner
							identer={[ident]}
							isMultiple={false}
							inkluderPartnere={inkluderPartnere}
							setInkluderPartnere={setInkluderPartnere}
						/>
					)}
				</div>
				<FolkeregisteretVisning data={personData} />
				<TjenestepensjonsavtaleVisning data={personData?.tenorRelasjoner?.tjenestepensjonavtale} />
				<EnhetsregisteretForetaksregisteretVisning
					data={_.get(personData, 'tenorRelasjoner.brreg-er-fr')}
				/>
				<SkattemeldingVisning data={personData?.tenorRelasjoner?.skattemelding} />
				<InntektVisning data={personData?.tenorRelasjoner?.inntekt} />
				<OrganisasjonArbeidsforholdVisning data={personData?.tenorRelasjoner?.arbeidsforhold} />
			</Box>
		</PersonVisningWrapper>
	)
}
