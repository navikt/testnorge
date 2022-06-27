import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import Loading from '~/components/ui/loading/Loading'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { InntektVisning } from './partials/InntektVisning'
import { FradragVisning } from './partials/FradragVisning'
import { ForskuddstrekkVisning } from './partials/ForskuddstrekkVisning'
import { ArbeidsforholdVisning } from './partials/ArbeidsforholdVisning'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import Formatters from '~/utils/DataFormatter'
import Panel from '~/components/ui/panel/Panel'

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
	return (
		<DollyFieldArray
			header="Inntektsinformasjon"
			getHeader={getHeader}
			data={sortedData}
			expandable={numInntekter > 1}
		>
			{(inntektsinformasjon: Inntektsinformasjon) => (
				<React.Fragment>
					<div className="person-visning_content">
						<TitleValue title="År/måned" value={inntektsinformasjon.aarMaaned} />
						<TitleValue
							title="Rapporteringstidspunkt"
							value={Formatters.formatDateTime(inntektsinformasjon.rapporteringsdato)}
						/>
						<TitleValue title="Virksomhet (orgnr/id)" value={inntektsinformasjon.virksomhet} />
						<TitleValue
							title="Opplysningspliktig (orgnr/id)"
							value={inntektsinformasjon.opplysningspliktig}
						/>
					</div>
					<InntektVisning data={inntektsinformasjon.inntektsliste} />
					<FradragVisning data={inntektsinformasjon.fradragsliste} />
					<ForskuddstrekkVisning data={inntektsinformasjon.forskuddstrekksliste} />
					<ArbeidsforholdVisning data={inntektsinformasjon.arbeidsforholdsliste} />
				</React.Fragment>
			)}
		</DollyFieldArray>
	)
}

export const InntektstubVisning = ({ liste, loading }: InntekstubVisning) => {
	if (loading) return <Loading label="Laster Inntektstub-data" />
	if (!liste) return null

	const foersteAar = liste[0]?.aarMaaned?.substring(0, 4)

	const sortedData = liste.slice().reverse()
	const numInntekter = sortedData?.length

	const sisteAar = sortedData[0]?.aarMaaned?.substring(0, 4)
	const inntektsperiode = foersteAar === sisteAar ? foersteAar : `${foersteAar} - ${sisteAar}`

	return (
		<div>
			<SubOverskrift label="A-ordningen (Inntektstub)" iconKind="inntektstub" />
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
		</div>
	)
}
