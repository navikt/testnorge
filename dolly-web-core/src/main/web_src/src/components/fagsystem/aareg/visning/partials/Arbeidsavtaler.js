import React from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import Loading from '~/components/ui/loading/Loading'

export const Arbeidsavtaler = ({ data, loading }) => {
	if (loading) return <Loading label="laster Aareg-data" />
	if (!data || data.length === 0) return false

	return (
		<div>
			<h4>Arbeidsavtaler</h4>
			<div>
				{data.map((id, idx) => (
					<div key={idx} className="person-visning_content">
						<TitleValue title="Antall timer per uke" value={id.antallTimerPerUke} />
						<TitleValue title="Arbeidstidsordning" value={id.arbeidstidsordning} />
						{/* Denne korresponderer med antall konverterte timer */}
						<TitleValue title="Beregnet antall timer per uke" value={id.beregnetAntallTimerPrUke} />
						{/* <TitleValue title="avloeningstype" value={id.avloeningstype} /> Denne burde være her, men er ikke populert i svaret. */}
						<TitleValue
							title="stillingsprosent siste endringsdato"
							value={Formatters.formatStringDates(id.sistStillingsendring)}
						/>
						{/* <TitleValue title="Lønning siste endringsdato" value={id.sisteLoennsendringsdato} /> Får ikke lov til å sette i bestilling */}
						<TitleValue title="Stillingsprosent" value={id.stillingsprosent} />
						<TitleValue title="Yrke" value={id.yrke} kodeverk="Yrker" />
					</div>
				))}
			</div>
		</div>
	)
}
