import { Alert, Box } from '@navikt/ds-react'
import React from 'react'
import styled from 'styled-components'
import Loading from '@/components/ui/loading/Loading'
import { EnhetsregisteretVisning } from '@/pages/organisasjoner/OrganisasjonTenorSoek/resultatVisning/EnhetsregisteretVisning'
import { OrganisasjonArbeidsforholdVisning } from '@/pages/organisasjoner/OrganisasjonTenorSoek/resultatVisning/OrganisasjonArbeidsforholdVisning'

type OrganisasjonVisningProps = {
	organisasjon: any
	orgnummer: string
	loading: boolean
	error: any
}

export type TenorOrganisasjon = {
	navn: string
	organisasjonsnummer: string
	organisasjonsform: {
		kode: string
		beskrivelse: string
	}
	tenorMetadata?: any
	forretningsadresse: {
		land: string
		landkode: string
		postnummer: string
		poststed: string
		adresse: string[]
		kommune: string
		kommunenummer: string
	}
	postadresse: {
		land: string
		landkode: string
		postnummer: string
		poststed: string
		adresse: string[]
		kommune: string
		kommunenummer: string
	}
	kilder: string[]
	naeringskoder: {
		kode: string
		beskrivelse: string
		hjelpeenhetskode: boolean
		rekkefolge: number
		nivaa: number
	}[]
	registreringsdatoEnhetsregisteret: string
	slettetIEnhetsregisteret: string
	registrertIForetaksregisteret: string
	slettetIForetaksregisteret: string
	registreringspliktigForetaksregisteret: string
	registrertIFrivillighetsregisteret: string
	registrertIStiftelsesregisteret: string
	registrertIMvaregisteret: string
	konkurs: string
	underAvvikling: string
	underTvangsavviklingEllerTvangsopplosning: string
	maalform: string
	ansvarsbegrensning: string
	harAnsatte: string
	antallAnsatte: number
	underenhet: {
		hovedenhet: string
		oppstartsdato: string
	}
	bedriftsforsamling: string
	representantskap: string
	enhetstatuser: any[]
	fullmakter: any[]
	kapital: {
		antallAksjer: string
		fritekst: any[]
		sakkyndigRedegjorelse: string
	}
	kjonnsrepresentasjon: string
	matrikkelnummer: any[]
	fravalgAvRevisjon: {
		fravalg: string
	}
	norskregistrertUtenlandskForetak: {
		helNorskEierskap: string
		aktivitetINorge: string
	}
	lovgivningOgForetaksformIHjemlandet: {
		foretaksform: string
	}
	registerIHjemlandet: {
		navnRegister: any[]
		adresse: any[]
	}
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

	const OrganisasjonData: TenorOrganisasjon = organisasjon.data?.organisasjoner?.find(
		(dokument: any) => dokument.organisasjonsnummer?.includes(orgnummer),
	)

	return (
		<OrganisasjonVisningWrapper>
			<Box background="surface-default" padding="3" borderRadius="medium">
				<div className="flexbox--space">
					<NavnHeader>{OrganisasjonData?.navn}</NavnHeader>
				</div>
				<EnhetsregisteretVisning data={OrganisasjonData} />
				<OrganisasjonArbeidsforholdVisning data={OrganisasjonData} />
			</Box>
		</OrganisasjonVisningWrapper>
	)
}
