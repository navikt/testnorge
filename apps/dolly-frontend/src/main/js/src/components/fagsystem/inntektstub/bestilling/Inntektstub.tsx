import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestillingsveileder/stegVelger/steg/steg3/Bestillingsvisning'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDateTime } from '@/utils/DataFormatter'
import { InntektstubKodeverk } from '@/config/kodeverk'
import { InntektFelter } from '@/components/fagsystem/inntektstub/visning/partials/InntektVisning'

type InntektstubTypes = {
	inntektstub: {
		inntektsinformasjon: Array<any>
	}
}

type InntektsinfoTypes = {
	inntektsinfo: any
	idx: number
	whiteBackground?: boolean
}

const Inntektsinformasjon = ({ inntektsinfo, idx, whiteBackground = false }: InntektsinfoTypes) => {
	return (
		<React.Fragment key={idx}>
			<TitleValue title="År/måned" value={inntektsinfo?.sisteAarMaaned} />
			<TitleValue title="Generer antall måneder" value={inntektsinfo?.antallMaaneder} />
			<TitleValue
				title="Rapporteringstidspunkt"
				value={formatDateTime(inntektsinfo?.rapporteringsdato)}
			/>
			<TitleValue title="Virksomhet (orgnr/id)" value={inntektsinfo?.virksomhet} />
			<TitleValue title="Opplysningspliktig (orgnr/id)" value={inntektsinfo?.opplysningspliktig} />
			{inntektsinfo?.inntektsliste?.length > 0 && (
				<DollyFieldArray
					header="Inntekter per måned"
					data={inntektsinfo?.inntektsliste}
					whiteBackground={whiteBackground}
					nested
				>
					{(inntekt: any, idy: number) => <InntektFelter data={inntekt} key={`inntekt_${idy}`} />}
				</DollyFieldArray>
			)}
			{inntektsinfo?.fradragsliste?.length > 0 && (
				<DollyFieldArray
					header="Fradrag"
					data={inntektsinfo?.fradragsliste}
					whiteBackground={whiteBackground}
					nested
				>
					{(fradrag: any, idy: number) => (
						<React.Fragment key={`fradrag_${idy}`}>
							<TitleValue title="Beløp" value={fradrag?.beloep} />
							<TitleValue
								title="Beskrivelse"
								value={fradrag?.beskrivelse}
								kodeverk={InntektstubKodeverk.Fradragbeskrivelse}
							/>
						</React.Fragment>
					)}
				</DollyFieldArray>
			)}
			{inntektsinfo?.forskuddstrekksliste?.length > 0 && (
				<DollyFieldArray
					header="Forskuddstrekk"
					data={inntektsinfo?.forskuddstrekksliste}
					whiteBackground={whiteBackground}
					nested
				>
					{(forskuddstrekk: any, idy: number) => (
						<React.Fragment key={`forskuddstrekk_${idy}`}>
							<TitleValue title="Beløp" value={forskuddstrekk?.beloep} />
							<TitleValue
								title="Beskrivelse"
								value={forskuddstrekk?.beskrivelse}
								kodeverk={InntektstubKodeverk.Forskuddstrekkbeskrivelse}
							/>
						</React.Fragment>
					)}
				</DollyFieldArray>
			)}
		</React.Fragment>
	)
}

export const Inntektstub = ({ inntektstub }: InntektstubTypes) => {
	if (!inntektstub || inntektstub?.inntektsinformasjon?.length === 0) {
		return null
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>A-ordningen (Inntektstub)</BestillingTitle>
				<DollyFieldArray header="Inntektsinformasjon" data={inntektstub.inntektsinformasjon}>
					{(inntektsinfo: any, idx: number) => (
						<Inntektsinformasjon inntektsinfo={inntektsinfo} idx={idx} />
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
