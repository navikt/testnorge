import { Alert, Box } from '@navikt/ds-react'
import React from 'react'
import styled from 'styled-components'
import Loading from '@/components/ui/loading/Loading'
import { EnhetsregisteretVisning } from '@/pages/organisasjoner/OrganisasjonTenorSoek/resultatVisning/EnhetsregisteretVisning'
import { OrganisasjonArbeidsforholdVisning } from '@/pages/organisasjoner/OrganisasjonTenorSoek/resultatVisning/OrganisasjonArbeidsforholdVisning'
import { OrganisasjonTestinnsendingSkattVisning } from '@/pages/organisasjoner/OrganisasjonTenorSoek/resultatVisning/OrganisasjonTestinnsendingSkattVisning'
import { OrganisasjonSamletReskontroinnsynVisning } from '@/pages/organisasjoner/OrganisasjonTenorSoek/resultatVisning/OrganisasjonSamletReskontroinnsynVisning'
import { OrganisasjonTjenestepensjonsavtaleVisning } from '@/pages/organisasjoner/OrganisasjonTenorSoek/resultatVisning/OrganisasjonTjenestepensjonsavtaleVisning'

type OrganisasjonVisningProps = {
	organisasjon: any
	orgnummer: string
	loading: boolean
	error: any
}

export type TenorOrganisasjon = {
	navn: string
	organisasjonsnummer: string
	tenorMetadata?: any
	brregKildedata?: any
	kilder?: string[]
	tenorRelasjoner: any
}

const OrganisasjonVisningWrapper = styled.div`
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
export const OrganisasjonTenorVisning = ({
	organisasjon,
	orgnummer,
	loading,
	error,
}: OrganisasjonVisningProps) => {
	if (loading) {
		return <Loading label="Laster organisasjon ..." />
	}

	if (error) {
		return <Alert variant="error">{`Feil ved henting av organisasjon: ${error}`}</Alert>
	}

	if (!organisasjon) {
		return null
	}

	const organisasjonData: TenorOrganisasjon = organisasjon.data?.organisasjoner?.find(
		(dokument: any) => dokument.organisasjonsnummer?.includes(orgnummer),
	)

	const brregData = organisasjonData?.brregKildedata
	const arbeidsforholdData = organisasjonData?.tenorRelasjoner?.arbeidsforhold
	const testinnsendingSkattEnhetData = organisasjonData?.tenorRelasjoner?.testinnsendingSkattEnhet
	const samletReskontroinnsynData = organisasjonData?.tenorRelasjoner?.samletReskontroinnsyn
	const tjenestepensjonData =
		organisasjonData?.tenorRelasjoner?.tjenestepensjonsavtaleOpplysningspliktig

	return (
		<OrganisasjonVisningWrapper>
			<Box background="default" padding="space-12" borderRadius="4">
				<div className="flexbox--space">
					<NavnHeader>{organisasjonData?.navn}</NavnHeader>
				</div>
				<EnhetsregisteretVisning data={brregData} />
				<OrganisasjonArbeidsforholdVisning data={arbeidsforholdData} />
				<OrganisasjonTestinnsendingSkattVisning data={testinnsendingSkattEnhetData} />
				<OrganisasjonSamletReskontroinnsynVisning data={samletReskontroinnsynData} />
				<OrganisasjonTjenestepensjonsavtaleVisning data={tjenestepensjonData} />
			</Box>
		</OrganisasjonVisningWrapper>
	)
}
