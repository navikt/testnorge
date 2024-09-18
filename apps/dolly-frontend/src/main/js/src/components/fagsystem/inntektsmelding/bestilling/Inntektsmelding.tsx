import React from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingData, BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { Kodeverk } from '@/components/fagsystem/inntektsmelding/InntektsmeldingTypes'
import { useInntektsmeldingKodeverk } from '@/utils/hooks/useInntektsmelding'
import { codeToNorskLabel, formatDate, oversettBoolean } from '@/utils/DataFormatter'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'

const showKodeverkLabel = (kodeverkstype: string, value: string) => {
	const { kodeverk, loading, error } = useInntektsmeldingKodeverk(kodeverkstype)
	if (loading || error) {
		return value
	}
	return kodeverk?.find((kode: any) => kode?.value === value)?.label || value
}

const AvtaltFerie = ({ ferie }) => {
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

const Refusjon = ({ refusjon }) => {
	console.log('refusjon: ', refusjon) //TODO - SLETT MEG
	console.log('isEmpty(refusjon): ', isEmpty(refusjon)) //TODO - SLETT MEG
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
				<DollyFieldArray header="Endring i refusjon" data={refusjon?.endringIRefusjonListe} nested>
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

const NaturalytelseOpphoer = ({ naturalytelseOpphoer }) => {
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

const NaturalytelseGjenopptakelse = ({ naturalytelseGjenopptakelse }) => {
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

export const Inntektsmelding = ({ inntektsmelding }) => {
	if (!inntektsmelding || inntektsmelding?.inntekter?.length === 0) {
		return null
	}

	return (
		<div className="person-visning">
			<ErrorBoundary>
				<BestillingTitle>Inntektsmelding (fra Altinn)</BestillingTitle>
				<DollyFieldArray header="Inntekt" data={inntektsmelding.inntekter}>
					{(inntekt: any, idx: number) => (
						<>
							<TitleValue
								title="Årsak til innsending"
								value={codeToNorskLabel(inntekt.aarsakTilInnsending)}
							/>
							<TitleValue title="Ytelse" value={codeToNorskLabel(inntekt?.ytelse)} />
							<TitleValue title="Nær relasjon" value={inntekt?.naerRelasjon ? 'Ja' : null} />
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
							<TitleValue
								title="Arbeidsforhold-ID"
								value={inntekt?.arbeidsforhold?.arbeidsforholdId}
							/>
							<TitleValue title="Beløp" value={inntekt?.arbeidsforhold?.beregnetInntekt?.beloep} />
							<TitleValue
								title="Årsak ved endring"
								value={codeToNorskLabel(inntekt?.arbeidsforhold?.aarsakVedEndring)}
							/>
							<TitleValue
								title="Første fraværsdag"
								value={formatDate(inntekt?.arbeidsforhold?.foersteFravaersdag)}
							/>
							<AvtaltFerie ferie={inntekt?.arbeidsforhold?.avtaltFerieListe} />
							<Refusjon refusjon={inntekt?.refusjon} />
							<NaturalytelseOpphoer naturalytelseOpphoer={inntekt?.opphoerAvNaturalytelseListe} />
							<NaturalytelseGjenopptakelse
								naturalytelseGjenopptakelse={inntekt?.gjenopptakelseNaturalytelseListe}
							/>
						</>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
