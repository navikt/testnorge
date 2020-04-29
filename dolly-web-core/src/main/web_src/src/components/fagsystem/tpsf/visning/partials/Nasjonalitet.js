import React from 'react'
import { PersoninformasjonKodeverk, AdresseKodeverk } from '~/config/kodeverk'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'

const Statsborgerskap = ({ statsborgerskap }) => (
	<div className="person-visning_content">
		<TitleValue
			title="Statsborgerskap"
			kodeverk={AdresseKodeverk.StatsborgerskapLand}
			value={statsborgerskap.statsborgerskap}
		/>
		<TitleValue
			title="Statsborgerskap fra"
			value={Formatters.formatDate(statsborgerskap.statsborgerskapRegdato)}
		/>
	</div>
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
				{statsborgerskap.length > 1 ? (
					<DollyFieldArray data={statsborgerskap} header="Statsborgerskap" nested>
						{(statsborgerskap, idx) => <Statsborgerskap statsborgerskap={statsborgerskap} />}
					</DollyFieldArray>
				) : (
					<Statsborgerskap statsborgerskap={statsborgerskap[0]} />
				)}
				<TitleValue title="Språk" kodeverk={PersoninformasjonKodeverk.Spraak} value={sprakKode} />
				<TitleValue
					title="Innvandret fra land"
					kodeverk={AdresseKodeverk.InnvandretUtvandretLand}
					value={innvandretFraLand}
				/>
				<TitleValue
					title="Innvandret dato"
					value={Formatters.formatDate(innvandretFraLandFlyttedato)}
				/>
				<TitleValue
					title="Utvandret til land"
					kodeverk={AdresseKodeverk.InnvandretUtvandretLand}
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
