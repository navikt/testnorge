import React from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { AdresseKodeverk } from '~/config/kodeverk'
import Formatters from '~/utils/DataFormatter'
import { Statsborgerskap } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { ArrayHistorikk } from '~/components/ui/historikk/ArrayHistorikk'

type StatsborgerskapProps = {
	data: Statsborgerskap
	idx?: number
}

type VisningProps = {
	statsborgerskapListe: [Statsborgerskap]
}

const StatsborgerskapVisning = ({ data, idx }: StatsborgerskapProps) => {
	if (data) {
		return (
			<div key={idx} className="person-visning_content">
				<TitleValue
					title="Statsborgerskap"
					kodeverk={AdresseKodeverk.StatsborgerskapLand}
					value={data.land}
				/>
				<TitleValue
					title="Statsborgerskap registrert"
					value={Formatters.formatDate(data.gyldigFraOgMed)}
				/>
				<TitleValue
					title="Statsborgerskap til"
					value={Formatters.formatDate(data.gyldigTilOgMed)}
				/>
			</div>
		)
	}
	return null
}

export const PdlStatsborgerskap = ({ statsborgerskapListe }: VisningProps) => {
	if (statsborgerskapListe?.length < 1) return null

	const gyldigeStatsborgerskap = statsborgerskapListe.filter(
		(borgerskap: Statsborgerskap) => !borgerskap.metadata?.historisk
	)
	const historiskeStatsborgerskap = statsborgerskapListe.filter(
		(borgerskap: Statsborgerskap) => borgerskap.metadata?.historisk
	)

	return (
		<ArrayHistorikk
			component={StatsborgerskapVisning}
			data={gyldigeStatsborgerskap}
			historiskData={historiskeStatsborgerskap}
			header="Statsborgerskap"
		/>
	)
}
