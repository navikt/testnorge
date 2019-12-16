import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'

export const Nasjonalitet = ({ data, visTittel = true }) => {
	const {
		statsborgerskap,
		sprakKode,
		innvandretFraLand,
		innvandretFraLandFlyttedato,
		utvandretTilLand,
		utvandretTilLandFlyttedato
	} = data

	return (
		<div>
			{visTittel && <SubOverskrift label="Nasjonalitet" />}
			<div className="person-visning_content">
				{statsborgerskap.map((sb, idx) => (
					<div key={idx}>
						<TitleValue title="Statsborgerskap" kodeverk="Landkoder" value={sb.statsborgerskap} />
						<TitleValue
							title="Statsborgerskap fra"
							value={Formatters.formatDate(sb.statsborgerskapRegdato)}
						/>
					</div>
				))}
				<TitleValue title="Språk" kodeverk="Språk" value={sprakKode} />
				<TitleValue title="Innvandret fra land" kodeverk="Landkoder" value={innvandretFraLand} />
				<TitleValue
					title="Innvandret dato"
					value={Formatters.formatDate(innvandretFraLandFlyttedato)}
				/>
				<TitleValue title="Utvandret til land" kodeverk="Landkoder" value={utvandretTilLand} />
				<TitleValue
					title="Utvandret dato"
					value={Formatters.formatDate(utvandretTilLandFlyttedato)}
				/>
			</div>
		</div>
	)
}
