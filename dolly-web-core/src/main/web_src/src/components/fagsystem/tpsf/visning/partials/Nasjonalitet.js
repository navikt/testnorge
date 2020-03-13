import React from 'react'
import Kodeverk from '~/utils/kodeverkMapper'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { Historikk } from '~/components/ui/historikk/Historikk'

const Statsborgerskap = ({ statsborgerskap }) => (
	<React.Fragment>
		<TitleValue
			title="Statsborgerskap"
			kodeverk={Kodeverk.statsborgerskapLand}
			value={statsborgerskap.statsborgerskap}
		/>
		<TitleValue
			title="Statsborgerskap fra"
			value={Formatters.formatDate(statsborgerskap.statsborgerskapRegdato)}
		/>
	</React.Fragment>
)

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
			{visTittel && <SubOverskrift label="Nasjonalitet" iconKind="nasjonalitet" />}
			<div className="person-visning_content">
				<Historikk component={Statsborgerskap} data={statsborgerskap} propName="statsborgerskap" />
				<TitleValue title="Språk" kodeverk={Kodeverk.sprak} value={sprakKode} />
				<TitleValue
					title="Innvandret fra land"
					kodeverk={Kodeverk.innvandretUtvandretLand}
					value={innvandretFraLand}
				/>
				<TitleValue
					title="Innvandret dato"
					value={Formatters.formatDate(innvandretFraLandFlyttedato)}
				/>
				<TitleValue
					title="Utvandret til land"
					kodeverk={Kodeverk.innvandretUtvandretLand}
					value={utvandretTilLand}
				/>
				<TitleValue
					title="Utvandret dato"
					value={Formatters.formatDate(utvandretTilLandFlyttedato)}
				/>
			</div>
		</div>
	)
}
