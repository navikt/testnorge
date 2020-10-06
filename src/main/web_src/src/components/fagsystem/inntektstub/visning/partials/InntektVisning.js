import React from 'react'
import { AdresseKodeverk } from '~/config/kodeverk'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import texts from '~/components/inntektStub/texts'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

export const InntektVisning = ({ data }) => {
	if (!data || data.length === 0) return null

	return (
		<React.Fragment>
			<h4>Inntekter</h4>
			<ErrorBoundary>
				<DollyFieldArray data={data} nested>
					{(id, idx) => (
						<div className="person-visning_content" key={idx}>
							<TitleValue title="Inntektstype" value={texts(id.inntektstype)} />
							<TitleValue title="Beløp" value={id.beloep} />
							<TitleValue
								title="Start opptjeningsperiode"
								value={Formatters.formatStringDates(id.startOpptjeningsperiode)}
							/>
							<TitleValue
								title="Slutt opptjeningsperiode"
								value={Formatters.formatStringDates(id.sluttOpptjeningsperiode)}
							/>
							<TitleValue
								title="Inngår i grunnlag for trekk"
								value={texts(id.inngaarIGrunnlagForTrekk)}
							/>
							<TitleValue
								title="Utløser arbeidsgiveravgift"
								value={texts(id.utloeserArbeidsgiveravgift)}
							/>
							<TitleValue title="Fordel" value={texts(id.fordel)} />
							<TitleValue title="Skatte- og avgiftsregel" value={texts(id.skatteOgAvgiftsregel)} />
							<TitleValue
								title="Skattemessig bosatt i land"
								value={id.skattemessigBosattILand}
								kodeverk={AdresseKodeverk.ArbeidOgInntektLand}
							/>
							<TitleValue
								title="Opptjeningsland"
								value={id.opptjeningsland}
								kodeverk={AdresseKodeverk.ArbeidOgInntektLand}
							/>
							<TitleValue title="Beskrivelse" value={texts(id.beskrivelse)} />
							{id.tilleggsinformasjon && (
								<React.Fragment>
									<TitleValue
										title="Tilleggsinformasjonstype"
										value={texts([Object.keys(id.tilleggsinformasjon)[0]])}
									/>
									{id.tilleggsinformasjon.bonusFraForsvaret && (
										<TitleValue
											title="År for utbetaling"
											value={id.tilleggsinformasjon.bonusFraForsvaret.aaretUtbetalingenGjelderFor}
										/>
									)}
									{id.tilleggsinformasjon.etterbetalingsperiode && (
										<React.Fragment>
											<TitleValue
												title="Etterbetaling start"
												value={Formatters.formatStringDates(
													id.tilleggsinformasjon.etterbetalingsperiode.startdato
												)}
											/>
											<TitleValue
												title="Etterbetaling slutt"
												value={Formatters.formatStringDates(
													id.tilleggsinformasjon.etterbetalingsperiode.sluttdato
												)}
											/>
										</React.Fragment>
									)}

									{id.tilleggsinformasjon.pensjon && (
										<React.Fragment>
											<TitleValue
												title="Grunnpensjonsbeløp"
												value={id.tilleggsinformasjon.pensjon.grunnpensjonsbeloep}
											/>
											<TitleValue
												title="Herav etterlattepensjon"
												value={id.tilleggsinformasjon.pensjon.heravEtterlattepensjon}
											/>
											<TitleValue
												title="Pensjonsgrad"
												value={id.tilleggsinformasjon.pensjon.pensjonsgrad}
											/>
											<TitleValue
												title="Startdato"
												value={Formatters.formatStringDates(
													id.tilleggsinformasjon.pensjon.tidsrom.startdato
												)}
											/>
											<TitleValue
												title="Sluttdato"
												value={Formatters.formatStringDates(
													id.tilleggsinformasjon.pensjon.tidsrom.sluttdato
												)}
											/>
											<TitleValue
												title="Tilleggspensjonsbeløp"
												value={id.tilleggsinformasjon.pensjon.tilleggspensjonsbeloep}
											/>
											<TitleValue
												title="Uføregrad"
												value={id.tilleggsinformasjon.pensjon.ufoeregrad}
											/>
										</React.Fragment>
									)}
									{id.tilleggsinformasjon.reiseKostOgLosji && (
										<TitleValue
											title="Persontype"
											value={texts(id.tilleggsinformasjon.reiseKostOgLosji.persontype)}
										/>
									)}
									{id.tilleggsinformasjon.inntjeningsforhold && (
										<TitleValue
											title="Inntjeningsforhold"
											value={texts(id.tilleggsinformasjon.inntjeningsforhold.inntjeningsforhold)}
										/>
									)}
								</React.Fragment>
							)}
							<TitleValue title="Antall" value={id.antall} />
						</div>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</React.Fragment>
	)
}
