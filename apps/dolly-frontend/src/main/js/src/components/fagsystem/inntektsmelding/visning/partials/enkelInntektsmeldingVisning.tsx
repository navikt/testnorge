import React from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import Formatters from '~/utils/DataFormatter'
import ArbeidsforholdVisning from './arbeidsforholdVisning'
import OmsorgspengerVisning from './omsorgspengerVisning'
import RefusjonVisning from './refusjonVisning'
import SykepengerVisning from './sykepengerVisning'
import PleiepengerVisning from './pleiepengerVisning'
import NaturalytelseVisning from './naturalytelseVisning'
import Button from '~/components/ui/button/Button'
import useBoolean from '~/utils/hooks/useBoolean'
import {
	EnkelInntektsmelding,
	Inntekt,
	Journalpost
} from '~/components/fagsystem/inntektsmelding/InntektsmeldingTypes'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { CodeView } from '~/components/codeView'

const getHeader = (data: Inntekt) => {
	const arbeidsgiver = data.arbeidsgiver
		? data.arbeidsgiver.virksomhetsnummer
		: data.arbeidsgiverPrivat
		? data.arbeidsgiverPrivat.arbeidsgiverFnr
		: ''
	return `Inntekt (${arbeidsgiver})`
}

export const EnkelInntektsmeldingVisning = ({ bestilling, data }: EnkelInntektsmelding) => {
	// Sjekk om bestillingid i bestilling er i data
	const journalpostidPaaBestilling = data.filter(
		id => id.bestillingId === bestilling.id || !id.bestillingId
	)

	return (
		<>
			<ErrorBoundary>
				<DollyFieldArray
					header="Inntekt"
					getHeader={getHeader}
					data={bestilling.data.inntektsmelding.inntekter}
					expandable={bestilling.data.inntektsmelding.inntekter.length > 1}
				>
					{(inntekt: Inntekt, idx: number) => (
						<>
							<div className="person-visning_content" key={idx}>
								<TitleValue
									title="Årsak til innsending"
									value={Formatters.codeToNorskLabel(inntekt.aarsakTilInnsending)}
								/>
								<TitleValue title="Ytelse" value={Formatters.codeToNorskLabel(inntekt.ytelse)} />
								<TitleValue
									title="Virksomhet (orgnr)"
									value={inntekt.arbeidsgiver && inntekt.arbeidsgiver.orgnummer}
								/>
								<TitleValue
									title="Opplysningspliktig virksomhet"
									value={inntekt.arbeidsgiver && inntekt.arbeidsgiver.virksomhetsnummer}
								/>
								<TitleValue
									title="Innsendingstidspunkt"
									value={Formatters.formatDate(inntekt.avsendersystem.innsendingstidspunkt)}
								/>
								<TitleValue
									title="Privat arbeidsgiver"
									value={inntekt.arbeidsgiverPrivat && inntekt.arbeidsgiverPrivat.arbeidsgiverFnr}
								/>
								<TitleValue title="Har nær relasjon" value={inntekt.naerRelasjon} />
								<TitleValue
									title="Startdato foreldrepenger"
									value={Formatters.formatDate(inntekt.startdatoForeldrepengeperiode)}
								/>
							</div>
							<ArbeidsforholdVisning data={inntekt.arbeidsforhold} />
							<OmsorgspengerVisning data={inntekt.omsorgspenger} />
							<RefusjonVisning data={inntekt.refusjon} />
							<SykepengerVisning data={inntekt.sykepengerIArbeidsgiverperioden} />
							<PleiepengerVisning data={inntekt.pleiepengerPerioder} />
							<NaturalytelseVisning
								data={inntekt.gjenopptakelseNaturalytelseListe}
								header="Gjenopptagekse av naturalytelse"
							/>
							<NaturalytelseVisning
								data={inntekt.opphoerAvNaturalytelseListe}
								header="Opphør av naturalytelse"
							/>
						</>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
			<ErrorBoundary>
				<DollyFieldArray data={journalpostidPaaBestilling} header="Journalpost-ID" nested>
					{(id: Journalpost, idx: number) => {
						const [viserSkjemainnhold, vis, skjul] = useBoolean(false)
						return (
							<div key={idx} className="person-visning_content">
								<TitleValue title="Miljø" value={id.miljoe.toUpperCase()} />
								<TitleValue title="Journalpost-id" value={id.dokumenter[0].journalpostId} />
								<TitleValue title="Dokumentinfo-id" value={id.dokumenter[0].dokumentInfoId} />

								<Button onClick={viserSkjemainnhold ? skjul : vis} kind="">
									{(viserSkjemainnhold ? 'SKJUL ' : 'VIS ') + 'SKJEMAINNHOLD'}
								</Button>
								{viserSkjemainnhold && <CodeView language="xml" code={id.dokumenter[0].dokument} />}
							</div>
						)
					}}
				</DollyFieldArray>
			</ErrorBoundary>
		</>
	)
}
