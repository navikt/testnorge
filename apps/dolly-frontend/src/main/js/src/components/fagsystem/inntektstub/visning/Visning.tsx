import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import Loading from '@/components/ui/loading/Loading'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { InntektVisning } from './partials/InntektVisning'
import { FradragVisning } from './partials/FradragVisning'
import { ForskuddstrekkVisning } from './partials/ForskuddstrekkVisning'
import { ArbeidsforholdVisning } from './partials/ArbeidsforholdVisning'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { formatDateTime } from '@/utils/DataFormatter'
import Panel from '@/components/ui/panel/Panel'
import { Alert } from '@navikt/ds-react'
import React from 'react'
import { useOrganisasjonForvalter } from '@/utils/hooks/useDollyOrganisasjoner'

type InntekstubVisning = {
	liste?: Array<Inntektsinformasjon>
	loading?: boolean
}

type InfoProps = {
	sortedData: Array<Inntektsinformasjon>
	numInntekter: number
}

type Inntektsinformasjon = {
	aarMaaned: string
	rapporteringsdato: string
	opplysningspliktig: string
	virksomhet: string
	inntektsliste: Array<unknown>
	fradragsliste?: Array<unknown>
	forskuddstrekksliste?: Array<unknown>
	arbeidsforholdsliste?: Array<unknown>
	versjon?: number
}

const getHeader = (data: Inntektsinformasjon) => {
	return `Inntektsinformasjon (${data.aarMaaned}) ${
		data.versjon ? ` - historikk (versjon ${data.versjon})` : ' '
	}`
}

const InntektsinformasjonVisning = ({ sortedData, numInntekter }: InfoProps) => {
	const virksomheter = useMemo(() => sortedData.map((data) => data.virksomhet), [sortedData])
	const opplysningspliktigeOrg = useMemo(
		() => sortedData.map((data) => data.opplysningspliktig),
		[sortedData],
	)
	const {
		organisasjoner: virksomhetInfo,
		loading: loadingVirksomheter,
		error: errorVirksomheter,
	} = useOrganisasjonForvalter(virksomheter)
	const {
		organisasjoner: opplysningspliktigInfo,
		loading: loadingOpplysninspliktige,
		error: errorOpplysningspliktige,
	} = useOrganisasjonForvalter(opplysningspliktigeOrg)

	if (loadingVirksomheter || loadingOpplysninspliktige) {
		return <Loading label="Laster organisasjonsdata" />
	} else if (errorVirksomheter || errorOpplysningspliktige) {
		return (
			<Alert variant={'error'} size={'small'} inline style={{ marginBottom: '20px' }}>
				Feil ved henting av organisasjonsdata:{' '}
				{errorOpplysningspliktige?.message || errorVirksomheter?.message}
			</Alert>
		)
	}

	return (
		<DollyFieldArray
			header="Inntektsinformasjon"
			getHeader={getHeader}
			data={sortedData}
			expandable={numInntekter > 1}
		>
			{(inntektsinformasjon: Inntektsinformasjon, index) => {
				const virksomhetNavn =
					virksomhetInfo?.[index]?.q1?.organisasjonsnavn ||
					virksomhetInfo?.[index]?.q2?.organisasjonsnavn
				const opplysningspliktigNavn =
					opplysningspliktigInfo?.[index]?.q1?.organisasjonsnavn ||
					opplysningspliktigInfo?.[index]?.q2?.organisasjonsnavn
				return (
					<React.Fragment>
						<div className="person-visning_content">
							<TitleValue title="År/måned" value={inntektsinformasjon.aarMaaned} />
							<TitleValue
								title="Virksomhet"
								value={`${inntektsinformasjon?.virksomhet} - ${virksomhetNavn}`}
							/>
							<TitleValue
								title="Opplysningspliktig"
								value={`${inntektsinformasjon.opplysningspliktig} - ${opplysningspliktigNavn}`}
							/>
							<TitleValue
								title="Rapporteringstidspunkt"
								value={formatDateTime(inntektsinformasjon.rapporteringsdato)}
							/>
						</div>
						<InntektVisning data={inntektsinformasjon.inntektsliste} />
						<FradragVisning data={inntektsinformasjon.fradragsliste} />
						<ForskuddstrekkVisning data={inntektsinformasjon.forskuddstrekksliste} />
						<ArbeidsforholdVisning data={inntektsinformasjon.arbeidsforholdsliste} />
					</React.Fragment>
				)
			}}
		</DollyFieldArray>
	)
}

export const InntektstubVisning = ({ liste, loading }: InntekstubVisning) => {
	if (loading) {
		return <Loading label="Laster Inntektstub-data" />
	}
	if (!liste) {
		return null
	}

	const foersteAar = liste[0]?.aarMaaned?.substring(0, 4)

	const sortedData = liste.slice().reverse()
	const numInntekter = sortedData?.length

	const sisteAar = sortedData[0]?.aarMaaned?.substring(0, 4)
	const inntektsperiode = foersteAar === sisteAar ? foersteAar : `${foersteAar} - ${sisteAar}`

	const manglerFagsystemdata = sortedData?.length < 1

	return (
		<div>
			<SubOverskrift
				label="A-ordningen (Inntektstub)"
				iconKind="pengesekk"
				isWarning={manglerFagsystemdata}
			/>
			{manglerFagsystemdata ? (
				<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
					Fant ikke inntekt-data på person
				</Alert>
			) : (
				<ErrorBoundary>
					{numInntekter > 5 ? (
						// @ts-ignore
						<Panel heading={`Inntektsinsinformasjon (${inntektsperiode})`}>
							<InntektsinformasjonVisning sortedData={sortedData} numInntekter={numInntekter} />
						</Panel>
					) : (
						<InntektsinformasjonVisning sortedData={sortedData} numInntekter={numInntekter} />
					)}
				</ErrorBoundary>
			)}
		</div>
	)
}
