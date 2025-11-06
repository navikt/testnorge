import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import React from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingData, BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, oversettBoolean } from '@/utils/DataFormatter'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { ArbeidKodeverk } from '@/config/kodeverk'
import {
	Diagnose,
	Helsepersonell,
	Periode,
	SykemeldingBestilling,
} from '@/components/fagsystem/sykdom/SykemeldingTypes'

type SykemeldingProps = {
	sykemelding: SykemeldingBestilling
}

export const Sykemelding = ({ sykemelding }: SykemeldingProps) => {
	if (!sykemelding || isEmpty(sykemelding)) {
		return null
	}

	const detaljertSykemelding = sykemelding.detaljertSykemelding
	const nySykemelding = sykemelding.nySykemelding

	const getNavn = (helsepersonell: Helsepersonell) => {
		return `${helsepersonell.fornavn} ${helsepersonell.mellomnavn ? helsepersonell.mellomnavn : ''} ${helsepersonell.etternavn}`
	}

	const getDiagnose = (diagnose: Diagnose) => {
		return `${diagnose.diagnosekode} - ${diagnose.diagnose}`
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Sykemelding</BestillingTitle>
				{detaljertSykemelding && (
					<div className="bestilling-blokk">
						<BestillingData>
							<TitleValue title="Startdato" value={formatDate(detaljertSykemelding.startDato)} />
							<TitleValue
								title="Trenger umiddelbar bistand"
								value={oversettBoolean(detaljertSykemelding.umiddelbarBistand)}
							/>
							<TitleValue
								title="Manglende tilrettelegging på arbeidsplassen"
								value={oversettBoolean(
									detaljertSykemelding.manglendeTilretteleggingPaaArbeidsplassen,
								)}
							/>
							<TitleValue
								title="Diagnose"
								value={getDiagnose(detaljertSykemelding.hovedDiagnose)}
								size="large"
							/>
						</BestillingData>
						{detaljertSykemelding?.biDiagnoser?.length > 0 && (
							<DollyFieldArray header="Bidiagnoser" data={detaljertSykemelding.biDiagnoser} nested>
								{(diagnose: Diagnose, idx: number) => (
									<React.Fragment key={idx}>
										<TitleValue title="Diagnose" value={getDiagnose(diagnose)} size="xlarge" />
									</React.Fragment>
								)}
							</DollyFieldArray>
						)}
						{detaljertSykemelding?.kontaktMedPasient && (
							<>
								<BestillingTitle>Tilbakedatering</BestillingTitle>
								<BestillingData>
									<TitleValue
										title="Begrunnelse ikke kontakt"
										value={detaljertSykemelding.kontaktMedPasient?.begrunnelseIkkeKontakt}
									/>
									<TitleValue
										title="Kontaktdato"
										value={formatDate(detaljertSykemelding.kontaktMedPasient?.kontaktDato)}
									/>
								</BestillingData>
							</>
						)}
						{detaljertSykemelding.helsepersonell && (
							<>
								<BestillingTitle>Helsepersonell</BestillingTitle>
								<BestillingData>
									<TitleValue title="Navn" value={getNavn(detaljertSykemelding.helsepersonell)} />
									<TitleValue title="Ident" value={detaljertSykemelding.helsepersonell.ident} />
									<TitleValue
										title="HPR-nummer"
										value={detaljertSykemelding.helsepersonell.hprId}
									/>
									<TitleValue
										title="SamhandlerType"
										value={detaljertSykemelding.helsepersonell.samhandlerType}
									/>
								</BestillingData>
							</>
						)}
						{detaljertSykemelding.arbeidsgiver && (
							<>
								<BestillingTitle>Arbeidsgiver</BestillingTitle>
								<BestillingData>
									<TitleValue title="Arbeidsgiver" value={detaljertSykemelding.arbeidsgiver.navn} />
									<TitleValue
										title="Yrkesbetegnelse"
										value={detaljertSykemelding.arbeidsgiver.yrkesbetegnelse}
										kodeverk={ArbeidKodeverk.Yrker}
									/>
									<TitleValue
										title="Stillingsprosent"
										value={detaljertSykemelding.arbeidsgiver.stillingsprosent}
									/>
								</BestillingData>
							</>
						)}
						{detaljertSykemelding.perioder?.length > 0 && (
							<DollyFieldArray header="Perioder" data={detaljertSykemelding.perioder} nested>
								{(periode: Periode, idx: number) => (
									<React.Fragment key={idx}>
										<TitleValue title="F.o.m. dato" value={formatDate(periode?.fom)} />
										<TitleValue title="T.o.m. dato" value={formatDate(periode?.tom)} />
										<TitleValue title="Aktivitet" value={periode?.aktivitet?.aktivitet} />
										<TitleValue
											title="Antall behandlingsdager"
											value={periode?.aktivitet?.behandlingsdager}
										/>
										<TitleValue title="Grad" value={periode?.aktivitet?.grad} />
										<TitleValue
											title="Har reisetilskudd"
											value={oversettBoolean(periode?.aktivitet?.reisetilskudd)}
										/>
									</React.Fragment>
								)}
							</DollyFieldArray>
						)}
						{!isEmpty(detaljertSykemelding.detaljer) && (
							<>
								<BestillingTitle>Detaljer</BestillingTitle>
								<BestillingData>
									<TitleValue
										title="Tiltak fra NAV"
										value={detaljertSykemelding.detaljer.tiltakNav}
									/>
									<TitleValue
										title="Tiltak på arbeidsplass"
										value={detaljertSykemelding.detaljer.tiltakArbeidsplass}
									/>
									<TitleValue
										title="Hensyn på arbeidsplass"
										value={detaljertSykemelding.detaljer.beskrivHensynArbeidsplassen}
									/>
									<TitleValue
										title="Arbeidsfør etter endt periode"
										value={oversettBoolean(
											detaljertSykemelding.detaljer.arbeidsforEtterEndtPeriode,
										)}
									/>
								</BestillingData>
							</>
						)}
					</div>
				)}
				{nySykemelding && (
					<BestillingData>
						<DollyFieldArray header="Periode" data={nySykemelding.aktivitet}>
							{(periode: Periode, idx: number) => (
								<React.Fragment key={idx}>
									<TitleValue title="F.o.m. dato" value={formatDate(periode?.fom)} />
									<TitleValue title="T.o.m. dato" value={formatDate(periode?.tom)} />
								</React.Fragment>
							)}
						</DollyFieldArray>
					</BestillingData>
				)}
			</ErrorBoundary>
		</div>
	)
}
