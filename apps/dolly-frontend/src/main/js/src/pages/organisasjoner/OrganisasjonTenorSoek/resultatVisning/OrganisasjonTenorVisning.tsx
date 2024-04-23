import { Alert, Box } from '@navikt/ds-react'
import React from 'react'
import styled from 'styled-components'
import Loading from '@/components/ui/loading/Loading'
import { EnhetsregisteretVisning } from '@/pages/organisasjoner/OrganisasjonTenorSoek/resultatVisning/EnhetsregisteretVisning'

type OrganisasjonVisningProps = {
	organisasjon: any
	orgnummer: string
	loading: boolean
	error: any
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

	const OrganisasjonData = organisasjon.data?.organisasjoner?.find((dokument: any) =>
		dokument.organisasjonsnummer?.includes(orgnummer),
	)

	return (
		<OrganisasjonVisningWrapper>
			<Box background="surface-default" padding="3" borderRadius="medium">
				<div className="flexbox--space">
					<NavnHeader>{OrganisasjonData?.navn}</NavnHeader>
				</div>
				<EnhetsregisteretVisning data={OrganisasjonData} />
			</Box>
		</OrganisasjonVisningWrapper>
	)
}
