import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { codeToNorskLabel, formatDate } from '@/utils/DataFormatter'
import ArbeidsforholdVisning from './arbeidsforholdVisning'
import OmsorgspengerVisning from './omsorgspengerVisning'
import RefusjonVisning from './refusjonVisning'
import SykepengerVisning from './sykepengerVisning'
import PleiepengerVisning from './pleiepengerVisning'
import NaturalytelseVisning from './naturalytelseVisning'
import {
	EnkelInntektsmelding,
	Inntekt,
	Journalpost,
} from '@/components/fagsystem/inntektsmelding/InntektsmeldingTypes'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { PersonVisningContent } from '@/components/fagsystem/inntektsmelding/visning/partials/personVisningContent'
import { useDokument, useJournalpost } from '@/utils/hooks/useJoarkDokument'

const getHeader = (data: Inntekt) => {
	const inntekt = data?.request?.inntekter?.[0]
	if (!inntekt) {
		return 'Inntekt'
	}
	const arbeidsgiver = inntekt.arbeidsgiver
		? inntekt.arbeidsgiver?.virksomhetsnummer
		: inntekt.arbeidsgiverPrivat
		? inntekt.arbeidsgiverPrivat?.arbeidsgiverFnr
		: ''
	return `Inntekt (${arbeidsgiver})`
}

export const EnkelInntektsmeldingVisning = ({ data }: EnkelInntektsmelding) => {
	if (!data) {
		return null
	}
	// Sjekk om bestillingid i bestilling er i data

	//TODO: Hente denne her istedemfor??
	// const journalpostidPaaBestilling = data.filter(
	// 	(id) => id.bestillingId === bestilling.id || !id.bestillingId,
	// )

	//TODO: Trenger vi denne??
	// const {
	// 	journalpost,
	// 	loading: loadingJournalpost,
	// 	error: errorJournalpost,
	// } = useJournalpost(data.dokument?.journalpostId, data.request?.miljoe)

	return (
		<>
			<ErrorBoundary>
				<DollyFieldArray
					header="Inntekt"
					getHeader={getHeader}
					data={data}
					expandable={data?.length > 3}
					whiteBackground
				>
					{(inntektsmelding: Inntekt, idx: number) => {
						const inntekt = inntektsmelding?.request?.inntekter?.[0]
						if (!inntekt) {
							return null
						}
						return (
							<>
								<div className="person-visning_content" key={idx}>
									<TitleValue
										title="Årsak til innsending"
										value={codeToNorskLabel(inntekt.aarsakTilInnsending)}
									/>
									<TitleValue title="Ytelse" value={codeToNorskLabel(inntekt.ytelse)} />
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
										value={formatDate(inntekt.avsendersystem.innsendingstidspunkt)}
									/>
									<TitleValue
										title="Privat arbeidsgiver"
										value={inntekt.arbeidsgiverPrivat && inntekt.arbeidsgiverPrivat.arbeidsgiverFnr}
									/>
									<TitleValue title="Har nær relasjon" value={inntekt.naerRelasjon} />
									<TitleValue
										title="Startdato foreldrepenger"
										value={formatDate(inntekt.startdatoForeldrepengeperiode)}
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
								<PersonVisningContent
									miljoe={inntektsmelding?.request?.miljoe}
									dokumentInfo={inntektsmelding?.dokument}
									index={idx}
								/>
							</>
						)
					}}
				</DollyFieldArray>
			</ErrorBoundary>
		</>
	)
}
