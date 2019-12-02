import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Arbeidsavtaler from './partials/Arbeidsavtaler'
import Permisjon from './partials/Permisjon'

export const AaregVisning = ({ data, loading }) => {
	if (!data) return false
	console.log('aareg visning data :', data)

	return (
		<div>
			<SubOverskrift label="Arbeidsforhold" />
			<div className="person-visning_content">
				{data.map((id, idx) => (
					<div key={idx}>
						<TitleValue title="" value={`#${idx + 1}`} size="x-small" />

						{id.ansettelsesperiode && (
							<TitleValue title="Startdato" value={id.ansettelsesperiode.periode.fom} />
						)}
						{id.ansettelsesperiode && (
							<TitleValue title="Startdato" value={id.ansettelsesperiode.periode.tom} />
						)}
						{/* antallTimerForTimeLoennet:
						 antall timer
						 periode fra og med
						 kanLeggeTilPerioderPerArbeidsforhold ??   MÅ VEL KANSKJE BESTILLES EKSPLISITT */}

						<Arbeidsavtaler data={id.arbeidsavtaler} />
						{/* <Permisjon data={id.permisjon} /> MÅ VEL KANSKJE BESTILLES EKSPLISITT */}

						{/* {id.arbeidsforholdtype && (
							<TitleValue title="Startdato" value={id.ansettelsesperiode.periode.tom} />
						)} HVOR ER DENNE? BURDE EKSISTERT*/}

						{id.arbeidsgiver && (
							<TitleValue title="Startdato" value={id.ansettelsesperiode.periode.tom} />
						)}

						{/* 
						X ansettelseperiode fra og med
						X arbeidsforholdtype (kodeverk)
						
						arbeidsgiver:
						 ORGNR vs PERS

						utenlandsopphold (kun en per arbeidsforhold)
						 land
						 periode fom og tom
						*/}

						{/* <TitleValue title="Stillingsprosent" value={id.arbeidsavtaler[0].bruksperiode.tom} /> */}
						<TitleValue title="Type av arbeidsgiver" value={id.ansettelsesperiode.periode.tom} />
						<TitleValue title="Arbeidsgiver ident" value={id.ansettelsesperiode.periode.tom} />
					</div>
				))}
			</div>
		</div>
	)
}
