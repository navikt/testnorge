import React from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import {
	BestillingData,
	BestillingTitle,
} from '@/components/bestilling/sammendrag/Bestillingsvisning'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { codeToNorskLabel, formatDate, oversettBoolean } from '@/utils/DataFormatter'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import {
	AvtaltFerie,
	BestillingVisning,
	Naturalytelse,
	Omsorgspenger,
	Pleiepenger,
	Refusjon,
	Sykepenger,
} from '@/components/fagsystem/inntektsmelding/InntektsmeldingTypes'

const AvtaltFerieVisning = ({ ferie }: { ferie: Array<AvtaltFerie> }) => {
	if (!ferie || ferie?.length === 0) {
		return null
	}

	return (
		<DollyFieldArray header={'Avtalt ferie'} data={ferie} nested>
			{(ferie: any, idx: number) => (
				<React.Fragment key={idx}>
					<TitleValue title="Fra og med dato" value={formatDate(ferie?.fom)} />
					<TitleValue title="Til og med dato" value={formatDate(ferie?.tom)} />
				</React.Fragment>
			)}
		</DollyFieldArray>
	)
}

const RefusjonVisning = ({ refusjon }: { refusjon: Refusjon }) => {
	if (!refusjon || isEmpty(refusjon)) {
		return null
	}

	return (
		<>
			<div className="flexbox--full-width" style={{ margin: '15px 0 -15px 0' }}>
				<BestillingTitle>Refusjon</BestillingTitle>
				<BestillingData>
					<TitleValue
						title="Samlet månedlig refusjonsbeløp"
						value={refusjon?.refusjonsbeloepPrMnd}
					/>
					<TitleValue
						title="Opphørsdato for refusjon"
						value={formatDate(refusjon?.refusjonsopphoersdato)}
					/>
				</BestillingData>
			</div>
			{refusjon?.endringIRefusjonListe?.length > 0 && (
				<DollyFieldArray
					header="Endringer i refusjon"
					data={refusjon?.endringIRefusjonListe}
					nested
				>
					{(endring: any, idx: number) => (
						<React.Fragment key={idx}>
							<TitleValue title="Endringsdato" value={formatDate(endring?.endringsdato)} />
							<TitleValue
								title="Nytt refusjonsbeløp per måned"
								value={endring?.refusjonsbeloepPrMnd}
							/>
						</React.Fragment>
					)}
				</DollyFieldArray>
			)}
		</>
	)
}

const NaturalytelseOpphoer = ({
	naturalytelseOpphoer,
}: {
	naturalytelseOpphoer: Array<Naturalytelse>
}) => {
	if (!naturalytelseOpphoer || naturalytelseOpphoer?.length === 0) {
		return null
	}

	return (
		<DollyFieldArray header="Opphør av naturalytelse" data={naturalytelseOpphoer} nested>
			{(naturalytelse: any, idx: number) => (
				<React.Fragment key={idx}>
					<TitleValue title="Beløp per måned" value={naturalytelse?.beloepPrMnd} />
					<TitleValue title="Fra og med dato" value={formatDate(naturalytelse?.fom)} />
					<TitleValue
						title="Årsak til innsending"
						value={codeToNorskLabel(naturalytelse?.naturalytelseType)}
					/>
				</React.Fragment>
			)}
		</DollyFieldArray>
	)
}

const NaturalytelseGjenopptakelse = ({
	naturalytelseGjenopptakelse,
}: {
	naturalytelseGjenopptakelse: Array<Naturalytelse>
}) => {
	if (!naturalytelseGjenopptakelse || naturalytelseGjenopptakelse?.length === 0) {
		return null
	}

	return (
		<DollyFieldArray
			header="Gjenopptakelse av naturalytelse"
			data={naturalytelseGjenopptakelse}
			nested
		>
			{(naturalytelse: any, idx: number) => (
				<React.Fragment key={idx}>
					<TitleValue title="Beløp per måned" value={naturalytelse?.beloepPrMnd} />
					<TitleValue title="Fra og med dato" value={formatDate(naturalytelse?.fom)} />
					<TitleValue
						title="Årsak til innsending"
						value={codeToNorskLabel(naturalytelse?.naturalytelseType)}
					/>
				</React.Fragment>
			)}
		</DollyFieldArray>
	)
}

const SykepengerIArbeidsgiverperioden = ({ sykepenger }: { sykepenger: Sykepenger }) => {
	if (!sykepenger || isEmpty(sykepenger)) {
		return null
	}

	return (
		<div className="flexbox--full-width" style={{ margin: '15px 0 -15px 0' }}>
			<BestillingTitle>Sykepenger</BestillingTitle>
			<BestillingData>
				<TitleValue title="Brutto utbetalt" value={sykepenger?.bruttoUtbetalt} />
				<TitleValue
					title="Begrunnelse for reduksjon eller ikke utbetalt"
					value={codeToNorskLabel(sykepenger?.begrunnelseForReduksjonEllerIkkeUtbetalt)}
				/>
				{sykepenger?.arbeidsgiverperiodeListe?.length > 0 && (
					<DollyFieldArray
						header="Arbeidsgiverperioder"
						data={sykepenger?.arbeidsgiverperiodeListe}
						nested
					>
						{(periode: any, idx: number) => (
							<React.Fragment key={idx}>
								<TitleValue title="Fra og med dato" value={formatDate(periode?.fom)} />
								<TitleValue title="Til og med dato" value={formatDate(periode?.tom)} />
							</React.Fragment>
						)}
					</DollyFieldArray>
				)}
			</BestillingData>
		</div>
	)
}

const Foreldrepenger = ({
	startdatoForeldrepengeperiode,
}: {
	startdatoForeldrepengeperiode: string
}) => {
	if (!startdatoForeldrepengeperiode) {
		return null
	}

	return (
		<div className="flexbox--full-width" style={{ margin: '15px 0 -15px 0' }}>
			<BestillingTitle>Foreldrepenger</BestillingTitle>
			<BestillingData>
				<TitleValue
					title="Startdato for periode"
					value={formatDate(startdatoForeldrepengeperiode)}
				/>
			</BestillingData>
		</div>
	)
}

const Pleiepengerperiode = ({
	pleiepengerperioder,
}: {
	pleiepengerperioder: Array<Pleiepenger>
}) => {
	if (!pleiepengerperioder || pleiepengerperioder?.length === 0) {
		return null
	}

	return (
		<DollyFieldArray header="Pleiepengerperioder" data={pleiepengerperioder} nested>
			{(periode: any, idx: number) => (
				<React.Fragment key={idx}>
					<TitleValue title="Fra og med dato" value={formatDate(periode?.fom)} />
					<TitleValue title="Til og med dato" value={formatDate(periode?.tom)} />
				</React.Fragment>
			)}
		</DollyFieldArray>
	)
}

const OmsorgspengerVisning = ({ omsorgspenger }: { omsorgspenger: Omsorgspenger }) => {
	if (!omsorgspenger || isEmpty(omsorgspenger)) {
		return null
	}

	return (
		<div className="flexbox--full-width" style={{ margin: '15px 0 -15px 0' }}>
			<BestillingTitle>Omsorgspenger</BestillingTitle>
			<BestillingData>
				<TitleValue
					title="Har utbetalt pliktige dager"
					value={oversettBoolean(omsorgspenger?.harUtbetaltPliktigeDager)}
				/>
				{omsorgspenger?.delvisFravaersListe?.length > 0 && (
					<DollyFieldArray header="Delvis fravær" data={omsorgspenger?.delvisFravaersListe} nested>
						{(fravaer: any, idx: number) => (
							<React.Fragment key={idx}>
								<TitleValue title="Dato" value={formatDate(fravaer?.dato)} />
								<TitleValue title="Antall timer" value={fravaer?.timer} />
							</React.Fragment>
						)}
					</DollyFieldArray>
				)}
				{omsorgspenger?.fravaersPerioder?.length > 0 && (
					<DollyFieldArray header="Fraværsperioder" data={omsorgspenger?.fravaersPerioder} nested>
						{(periode: any, idx: number) => (
							<React.Fragment key={idx}>
								<TitleValue title="Fra og med dato" value={formatDate(periode?.fom)} />
								<TitleValue title="Til og med dato" value={formatDate(periode?.tom)} />
							</React.Fragment>
						)}
					</DollyFieldArray>
				)}
			</BestillingData>
		</div>
	)
}

export const Inntektsmelding = ({ inntektsmelding }: BestillingVisning) => {
	if (!inntektsmelding || inntektsmelding?.inntekter?.length === 0) {
		return null
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Inntektsmelding (fra Altinn)</BestillingTitle>
				<DollyFieldArray header="Inntekt" data={inntektsmelding.inntekter}>
					{(inntekt: any, idx: number) => (
						<React.Fragment key={idx}>
							<TitleValue
								title="Årsak til innsending"
								value={codeToNorskLabel(inntekt.aarsakTilInnsending)}
							/>
							<TitleValue title="Ytelse" value={codeToNorskLabel(inntekt?.ytelse)} />
							<TitleValue
								title="Innsendingstidspunkt"
								value={formatDate(inntekt?.avsendersystem?.innsendingstidspunkt)}
							/>
							<TitleValue
								title="Arbeidsgiver (orgnr)"
								value={inntekt?.arbeidsgiver?.virksomhetsnummer}
							/>
							<TitleValue
								title="Arbeidsgiver (fnr/dnr/npid)"
								value={inntekt?.arbeidsgiverPrivat?.arbeidsgiverFnr}
							/>
							<TitleValue title="Nær relasjon" value={inntekt?.naerRelasjon ? 'Ja' : null} />
							<TitleValue
								title="Arbeidsforhold-ID"
								value={inntekt?.arbeidsforhold?.arbeidsforholdId}
							/>
							<TitleValue title="Beløp" value={inntekt?.arbeidsforhold?.beregnetInntekt?.beloep} />
							<TitleValue
								title="Årsak ved endring"
								value={codeToNorskLabel(inntekt?.arbeidsforhold?.beregnetInntekt?.aarsakVedEndring)}
							/>
							<TitleValue
								title="Første fraværsdag"
								value={formatDate(inntekt?.arbeidsforhold?.foersteFravaersdag)}
							/>
							<AvtaltFerieVisning ferie={inntekt?.arbeidsforhold?.avtaltFerieListe} />
							<RefusjonVisning refusjon={inntekt?.refusjon} />
							<NaturalytelseOpphoer naturalytelseOpphoer={inntekt?.opphoerAvNaturalytelseListe} />
							<NaturalytelseGjenopptakelse
								naturalytelseGjenopptakelse={inntekt?.gjenopptakelseNaturalytelseListe}
							/>
							<SykepengerIArbeidsgiverperioden
								sykepenger={inntekt?.sykepengerIArbeidsgiverperioden}
							/>
							<Foreldrepenger
								startdatoForeldrepengeperiode={inntekt?.startdatoForeldrepengeperiode}
							/>
							<Pleiepengerperiode pleiepengerperioder={inntekt?.pleiepengerPerioder} />
							<OmsorgspengerVisning omsorgspenger={inntekt?.omsorgspenger} />
						</React.Fragment>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
