import { AdresseKodeverk } from '@/config/kodeverk'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate } from '@/utils/DataFormatter'
import texts from '@/components/fagsystem/inntektstub/validerInntekt/texts'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import React from 'react'

export const InntektFelter = ({ data }: { data: any }) => {
	return (
		<>
			<TitleValue title="Beløp" value={data.beloep} />
			<TitleValue title="Inntektstype" value={texts(data.inntektstype)} />
			<TitleValue title="Beskrivelse" value={texts(data.beskrivelse)} />
			<TitleValue
				title="Inngår i grunnlag for trekk"
				value={texts(data.inngaarIGrunnlagForTrekk)}
			/>
			<TitleValue
				title="Utløser arbeidsgiveravgift"
				value={texts(data.utloeserArbeidsgiveravgift)}
			/>
			<TitleValue title="Fordel" value={texts(data.fordel)} />
			<TitleValue title="Skatte- og avgiftsregel" value={texts(data.skatteOgAvgiftsregel)} />
			<TitleValue
				title="Skattemessig bosatt i land"
				value={data.skattemessigBosattILand}
				kodeverk={AdresseKodeverk.ArbeidOgInntektLand}
			/>
			<TitleValue
				title="Opptjeningsland"
				value={data.opptjeningsland}
				kodeverk={AdresseKodeverk.ArbeidOgInntektLand}
			/>
			<TitleValue title="Antall" value={data.antall} />
			{data.tilleggsinformasjon && (
				<React.Fragment>
					<TitleValue
						title="Tilleggsinformasjonstype"
						value={texts([Object.keys(data.tilleggsinformasjon)[0]])}
					/>
					{data.tilleggsinformasjon.bonusFraForsvaret && (
						<TitleValue
							title="År for utbetaling"
							value={data.tilleggsinformasjon.bonusFraForsvaret.aaretUtbetalingenGjelderFor}
						/>
					)}
					{data.tilleggsinformasjon.etterbetalingsperiode && (
						<React.Fragment>
							<TitleValue
								title="Etterbetaling start"
								value={formatDate(data.tilleggsinformasjon.etterbetalingsperiode.startdato)}
							/>
							<TitleValue
								title="Etterbetaling slutt"
								value={formatDate(data.tilleggsinformasjon.etterbetalingsperiode.sluttdato)}
							/>
						</React.Fragment>
					)}

					{data.tilleggsinformasjon.pensjon && (
						<React.Fragment>
							<TitleValue
								title="Grunnpensjonsbeløp"
								value={data.tilleggsinformasjon.pensjon.grunnpensjonsbeloep}
							/>
							<TitleValue
								title="Herav etterlattepensjon"
								value={data.tilleggsinformasjon.pensjon.heravEtterlattepensjon}
							/>
							<TitleValue
								title="Pensjonsgrad"
								value={data.tilleggsinformasjon.pensjon.pensjonsgrad}
							/>
							<TitleValue
								title="Startdato"
								value={formatDate(data.tilleggsinformasjon.pensjon.tidsrom?.startdato)}
							/>
							<TitleValue
								title="Sluttdato"
								value={formatDate(data.tilleggsinformasjon.pensjon.tidsrom?.sluttdato)}
							/>
							<TitleValue
								title="Tilleggspensjonsbeløp"
								value={data.tilleggsinformasjon.pensjon.tilleggspensjonsbeloep}
							/>
							<TitleValue title="Uføregrad" value={data.tilleggsinformasjon.pensjon.ufoeregrad} />
						</React.Fragment>
					)}
					{data.tilleggsinformasjon.reiseKostOgLosji && (
						<TitleValue
							title="Persontype"
							value={texts(data.tilleggsinformasjon.reiseKostOgLosji.persontype)}
						/>
					)}
					{data.tilleggsinformasjon.inntjeningsforhold && (
						<TitleValue
							title="Inntjeningsforhold"
							value={texts(data.tilleggsinformasjon.inntjeningsforhold.inntjeningsforhold)}
						/>
					)}
				</React.Fragment>
			)}
			<TitleValue
				title="Start opptjeningsperiode"
				value={formatDate(data.startOpptjeningsperiode)}
			/>
			<TitleValue
				title="Slutt opptjeningsperiode"
				value={formatDate(data.sluttOpptjeningsperiode)}
			/>
		</>
	)
}

export const InntektVisning = ({ data }: { data: any }) => {
	if (!data || data.length === 0) {
		return null
	}

	return (
		<React.Fragment>
			<h4>Inntekter</h4>
			<ErrorBoundary>
				<DollyFieldArray data={data} nested>
					{(id: any, idx: string) => (
						<div className="person-visning_content" key={idx}>
							<InntektFelter data={id} />
						</div>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</React.Fragment>
	)
}
