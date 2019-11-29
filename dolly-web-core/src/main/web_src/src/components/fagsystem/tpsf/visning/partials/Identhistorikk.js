import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'

export const Identhistorikk = ({ identhistorikk, visTittel = true }) => {
	if (!identhistorikk) return false

	return (
		<div>
			{visTittel && <SubOverskrift label="Identhistorikk" />}
			{identhistorikk.map(({ aliasPerson, regdato }, idx) => (
				<div key={idx} className="person-visning_content">
					<TitleValue title="" value={`#${idx + 1}`} size="x-small" />
					<TitleValue title="Identtype" value={aliasPerson.identtype} />
					<TitleValue title="Ident" value={aliasPerson.ident} />
					<TitleValue title="Kjønn" value={Formatters.kjonnToString(aliasPerson.kjonn)} />
					<TitleValue title="Utgått dato" value={Formatters.formatDate(regdato)} />
				</div>
			))}
		</div>
	)
}
