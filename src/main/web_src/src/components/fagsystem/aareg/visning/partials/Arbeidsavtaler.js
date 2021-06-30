import React from 'react'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { ArbeidKodeverk } from '~/config/kodeverk'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

export const Arbeidsavtaler = ({ data }) => {
	if (!data || data.length === 0) return null
	const detaljer = data[0]

	return (
		<React.Fragment>
			<h4>Ansettelsesdetaljer</h4>
			<ErrorBoundary>
				<div className="person-visning_content">
					<TitleValue title="Yrke" value={detaljer.yrke} kodeverk={ArbeidKodeverk.Yrker} />
					{/* //TODO: Ansettelsesform mangler fra Aareg */}
					<TitleValue
						title="Stillingsprosent"
						value={detaljer.stillingsprosent === 0 ? '0' : detaljer.stillingsprosent}
					/>
					{/* //TODO: Endringsdato stillingsprosent mangler fra Aareg */}
					<TitleValue
						title="Endringsdato lønn"
						value={Formatters.formatStringDates(detaljer.sistLoennsendring)}
					/>
					<TitleValue
						title="Arbeidstidsordning"
						value={detaljer.arbeidstidsordning}
						kodeverk={ArbeidKodeverk.Arbeidstidsordninger}
					/>
					<TitleValue
						title="Avtalte arbeidstimer per uke"
						value={detaljer.beregnetAntallTimerPrUke}
					/>
				</div>
			</ErrorBoundary>
		</React.Fragment>
	)
}
