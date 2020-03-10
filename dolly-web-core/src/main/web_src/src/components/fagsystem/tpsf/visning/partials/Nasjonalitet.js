import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'

export const Nasjonalitet = ({ data, visTittel = true }) => {
	const { statsborgerskap, sprakKode, innvandretUtvandret } = data

	return (
		<div>
			{visTittel && <SubOverskrift label="Nasjonalitet" iconKind="nasjonalitet" />}
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
			</div>
			<h3>Innvandring og utvandring</h3>

			<DollyFieldArray data={innvandretUtvandret}>
				{(id, idx) => (
					<React.Fragment>
						{innvandretUtvandret && (
							<>
								<TitleValue title="Inn/utvandret" value={innvandretUtvandret[idx].innutvandret} />
								<TitleValue
									title="Land"
									kodeverk="Landkoder"
									value={innvandretUtvandret[idx].landkode}
								/>
								<TitleValue
									title="Flytte dato"
									value={Formatters.formatDate(innvandretUtvandret[idx].flyttedato)}
								/>
							</>
						)}
					</React.Fragment>
				)}
			</DollyFieldArray>
			<InnvandretUtvandretCheck data={innvandretUtvandret} />
		</div>
	)
}

function InnvandretUtvandretCheck(innvandretUtvandret) {
	const siste = innvandretUtvandret.data.length - 1
	const innvandretEllerUtvandret = innvandretUtvandret.data[siste].innutvandret
	return innvandretEllerUtvandret
}
export default InnvandretUtvandretCheck
