import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'

export const partnerVisning = data => {
	return (
		<div>
			<SubOverskrift label="Partner" />
			{data.map((partnerInfo, idx) => (
				<div className="person-visning_content" key={idx}>
					<TitleValue title="PartnerNr" value={`#${idx + 1}`} size="x-small" />
					<TitleValue
						title="Kjønn"
						value={Formatters.showLabel('Identtype', partnerInfo.identtype)}
					/>
					<TitleValue title="Forhold" value={Formatters.showLabel('Kjønn', partnerInfo.kjonn)} />
					<TitleValue
						title="Forhold"
						value={Formatters.showLabel('Forhold', partnerInfo.sivilstand)}
					/>
					<TitleValue
						title="Adresse"
						value={Formatters.showLabel('Adresse', partnerInfo.harFellesAdresse)}
					/>
				</div>
			))}
		</div>
	)
}
