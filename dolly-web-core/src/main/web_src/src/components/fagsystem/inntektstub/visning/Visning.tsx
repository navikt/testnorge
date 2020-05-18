import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import Loading from '~/components/ui/loading/Loading'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { InntektVisning } from './partials/InntektVisning'
import { FradragVisning } from './partials/FradragVisning'
import { ForskuddstrekkVisning } from './partials/ForskuddstrekkVisning'
import { ArbeidsforholdVisning } from './partials/ArbeidsforholdVisning'

type InntekstubVisning = {
	liste?: Array<Inntektsinformasjon>
	loading?: boolean
}

type Inntektsinformasjon = {
	aarMaaned: string
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

export const InntektstubVisning = ({ liste, loading }: InntekstubVisning) => {
	if (loading) return <Loading label="Laster Inntektstub-data" />
	if (!liste) return null

	return (
		<div>
			<SubOverskrift label="A-ordningen (Inntektskomponenten)" iconKind="inntektstub" />
			<DollyFieldArray
				header="Inntektsinformasjon"
				getHeader={getHeader}
				data={liste}
				expandable={liste.length > 1}
			>
				{(inntektsinformasjon: Inntektsinformasjon) => (
					<React.Fragment>
						<div className="person-visning_content">
							<TitleValue title="År/måned" value={inntektsinformasjon.aarMaaned} />
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
		</div>
	)
}
