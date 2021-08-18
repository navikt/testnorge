import React from 'react'
import { EnhetData } from '../types'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'

type Detaljer = {
	data: EnhetData
}

export default ({ data }: Detaljer) => (
	<>
		<h4>Kontaktdata</h4>
		<div className="person-visning_content">
			<TitleValue title="Telefon" value={data.telefon} />
			<TitleValue title="E-postadresse" value={data.epost} />
			<TitleValue title="Internettadresse" value={data.nettside} />
		</div>
	</>
)
