import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, formatDateTime } from '@/utils/DataFormatter'
import { AdresseKodeverk, ArbeidKodeverk, InntektstubKodeverk } from '@/config/kodeverk'
import { EkspanderbarVisning } from '@/components/bestilling/sammendrag/visning/EkspanderbarVisning'
import texts from '@/components/inntektStub/texts'
import _get from 'lodash/get'

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

function genererTitleValueFelter(data: any) {
	return Object.entries(data)?.map(([key, value]) => {
		const erDato = !isNaN(Date.parse(value))
		if (
			erDato &&
			(key.includes('Dato') ||
				key.includes('dato') ||
				key.includes('Periode') ||
				key.includes('periode'))
		) {
			return <TitleValue title={texts(key)} value={formatDate(value)} />
		}
		if (key.includes('land') || key.includes('Land')) {
			return (
				<TitleValue
					title={texts(key)}
					value={value}
					kodeverk={AdresseKodeverk.ArbeidOgInntektLand}
				/>
			)
		}
		if (_get(value, key) && key === 'tilleggsinformasjon' && !data.tilleggsinformasjon) {
			return Object?.entries(value)?.map(([key, value]) => {
				return <TitleValue title="Tilleggsinformasjonstype" value={texts(key)} />
			})
		}
		if (typeof value === 'object') {
			return
		}
		return <TitleValue title={texts(key)} value={texts(value)} />
	})
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
					{(inntekt: any, idy: number) => (
						<React.Fragment key={`inntekt_${idy}`}>
							{genererTitleValueFelter(inntekt)}
						</React.Fragment>
					)}
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
			{inntektsinfo?.arbeidsforholdsliste?.length > 0 && (
				<DollyFieldArray
					header="Arbeidsforhold"
					data={inntektsinfo?.arbeidsforholdsliste}
					whiteBackground={whiteBackground}
					nested
				>
					{(arbeidsforhold: any, idy: number) => (
						<React.Fragment key={`arbeidsforhold_${idy}`}>
							<TitleValue
								title="Arbeidsforholdstype"
								value={arbeidsforhold.arbeidsforholdstype}
								kodeverk={ArbeidKodeverk.Arbeidsforholdstyper}
							/>
							<TitleValue title="Startdato" value={formatDate(arbeidsforhold.startdato)} />
							<TitleValue title="Sluttdato" value={formatDate(arbeidsforhold.sluttdato)} />
							<TitleValue
								title="Timer per uke (full stilling)"
								value={arbeidsforhold.antallTimerPerUkeSomEnFullStillingTilsvarer}
							/>
							<TitleValue title="Stillingsprosent" value={arbeidsforhold.stillingsprosent} />
							<TitleValue
								title="Avlønningstype"
								value={arbeidsforhold.avloenningstype}
								kodeverk={ArbeidKodeverk.Avloenningstyper}
							/>
							<TitleValue
								title="Yrke"
								value={arbeidsforhold.yrke}
								kodeverk={ArbeidKodeverk.Yrker}
							/>
							<TitleValue
								title="Arbeidstidsordning"
								value={arbeidsforhold.arbeidstidsordning}
								kodeverk={ArbeidKodeverk.Arbeidstidsordninger}
							/>
							<TitleValue
								title="Siste lønnsendringsdato"
								value={formatDate(arbeidsforhold.sisteLoennsendringsdato)}
							/>
							<TitleValue
								title="Stillingsprosentendring"
								value={formatDate(arbeidsforhold.sisteDatoForStillingsprosentendring)}
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
		<div className="person-visning">
			<ErrorBoundary>
				<BestillingTitle>A-ordningen (Inntektstub)</BestillingTitle>
				<DollyFieldArray header="Inntektsinformasjon" data={inntektstub.inntektsinformasjon}>
					{(inntektsinfo: any, idx: number) => (
						<>
							<Inntektsinformasjon inntektsinfo={inntektsinfo} idx={idx} />
							<EkspanderbarVisning
								vis={inntektsinfo?.historikk?.length > 0}
								header="INNTEKTSENDRING (HISTORIKK)"
							>
								<DollyFieldArray data={inntektsinfo.historikk} nested>
									{(historikk: any, idy: number) => (
										<Inntektsinformasjon
											inntektsinfo={historikk}
											idx={idy}
											whiteBackground={true}
										/>
									)}
								</DollyFieldArray>
							</EkspanderbarVisning>
						</>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
