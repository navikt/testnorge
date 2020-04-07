import React from 'react'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import Loading from '~/components/ui/loading/Loading'
import ArbeidsforholdVisning from './partials/arbeidsforholdVisning'
import OmsorgspengerVisning from './partials/omsorgspengerVisning'
import RefusjonVisning from './partials/refusjonVisning'

export const InntektsmeldingVisning = ({ bestilling, loading }) => {
	// export const InntektsmeldingVisning = ({ data, loading }) => {
	// if (loading) return <Loading label="laster inntektsmelding data" />
	// if (!data) return false

	// Midlertidig løsning der vi viser bestilte verdier istedenfor å lese tilbake fra kilde.
	// Data hentes egentlig som prop. Husk å fjerne at bestilling sendes fra PersonVisning.js også

	if (!bestilling.inntektsmelding) return false
	const data = bestilling.inntektsmelding
	return (
		<div>
			<SubOverskrift label="Inntektsmelding (fra Altinn)" iconKind="inntektsmelding" />
			<DollyFieldArray header="Inntekt" data={data.inntekter}>
				{(inntekt, idx) => (
					<>
						<div className="person-visning_content" key={idx}>
							<TitleValue title="Årsak til innsending" value={inntekt.aarsakTilInnsending} />
							<TitleValue title="Ytelse" value={inntekt.ytelse} />
							<TitleValue title="Virksomhet" value={inntekt.arbeidsgiver.orgnummer} />
							<TitleValue
								title="Opplysningspliktig virksomhet"
								value={inntekt.arbeidsgiver.virksomhetsnummer}
							/>
							<TitleValue
								title="Innsendingstidspunkt"
								value={Formatters.formatStringDates(inntekt.avsendersystem.dato)}
							/>
							<TitleValue title="Har nær relasjon" value={inntekt.naerRelasjon} />
						</div>
						<ArbeidsforholdVisning data={inntekt.arbeidsforhold} />
						<OmsorgspengerVisning data={inntekt.omsorgspenger} />
						<RefusjonVisning data={inntekt.refusjon} />
						{/* //TODO: Deler som mangler:
						Sykepengervisning
						ForeldrepengerVisning
						NaturalytelseVisning (opphørt og gjenopptakelse)
						PleiepengerVisning */}
					</>
				)}
			</DollyFieldArray>
		</div>
	)
}
