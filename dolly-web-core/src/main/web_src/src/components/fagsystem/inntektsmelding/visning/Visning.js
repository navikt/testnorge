import React from 'react'
import _set from 'lodash/set'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import Loading from '~/components/ui/loading/Loading'
import ArbeidsforholdVisning from './partials/arbeidsforholdVisning'
import OmsorgspengerVisning from './partials/omsorgspengerVisning'
import RefusjonVisning from './partials/refusjonVisning'
import SykepengerVisning from './partials/sykepengerVisning'
import PleiepengerVisning from './partials/pleiepengerVisning'
import NaturalytelseVisning from './partials/naturalytelseVisning'

export const InntektsmeldingVisning = ({ bestilling, loading }) => {
	//TODO: Gjøre om til tsx
	// export const InntektsmeldingVisning = ({ data, loading }) => {
	// if (loading) return <Loading label="laster inntektsmelding data" />
	// if (!data) return false

	// Midlertidig løsning der vi viser bestilte verdier istedenfor å lese tilbake fra kilde.
	// Data hentes egentlig som prop. Husk å fjerne at bestilling sendes fra PersonVisning.js også
	if (!bestilling || bestilling.inntektsmelding.length < 1) return false
	const inntektsmeldingData = bestilling.inntektsmelding

	return (
		<div>
			<SubOverskrift label="Inntektsmelding (fra Altinn)" iconKind="inntektsmelding" />
			{inntektsmeldingData.length > 1 ? (
				<DollyFieldArray header="Inntektsmeldinger" data={inntektsmeldingData} nested>
					{inntektsmelding => <Inntektsmelding header="Inntekt" data={inntektsmelding.inntekter} />}
				</DollyFieldArray>
			) : (
				<Inntektsmelding header="Inntekt" data={inntektsmeldingData[0].inntekter} />
			)}
		</div>
	)
}

const Inntektsmelding = ({ header, data }) => (
	<DollyFieldArray header={header} data={data}>
		{(inntekt, idx) => (
			<>
				<div className="person-visning_content" key={idx}>
					<TitleValue
						title="Årsak til innsending"
						value={Formatters.codeToNorskLabel(inntekt.aarsakTilInnsending)}
					/>
					<TitleValue title="Ytelse" value={Formatters.codeToNorskLabel(inntekt.ytelse)} />
					<TitleValue title="Virksomhet" value={inntekt.arbeidsgiver.orgnummer} />
					<TitleValue
						title="Opplysningspliktig virksomhet"
						value={inntekt.arbeidsgiver.virksomhetsnummer}
					/>
					<TitleValue
						title="Innsendingstidspunkt"
						value={Formatters.formatDate(inntekt.avsendersystem.innsendingstidspunkt)}
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
)
InntektsmeldingVisning.filterValues = (bestillinger, currentBestilling) => {
	if (!bestillinger || !currentBestilling) return false

	const inntektsmeldinger = bestillinger
		.map(bestilling => bestilling.inntektsmelding)
		.filter(inntektsmelding => inntektsmelding)
	return { ...currentBestilling.bestilling, inntektsmelding: inntektsmeldinger }
}
