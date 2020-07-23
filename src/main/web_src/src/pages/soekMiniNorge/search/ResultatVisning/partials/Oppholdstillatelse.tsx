import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { Oppholdstillatelse as OppholdstillatelseType } from '~/pages/soekMiniNorge/hodejegeren/types'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { OppholdstillatelseKodeverk } from '~/config/kodeverk'

interface OppholdstillatelseProps {
	data: OppholdstillatelseType
}

export const Oppholdstillatelse = ({ data }: OppholdstillatelseProps) => {
	if (!data || !data.status) return null

	return (
		<>
			<SubOverskrift label="Oppholdstillatelse" iconKind="udi" />
			<div className="person-visning_content">
				<TitleValue
					title="Status"
					kodeverk={OppholdstillatelseKodeverk.Oppholdstillatelser}
					value={data.status}
				/>
				<TitleValue title="Fra dato" value={Formatters.formatDate(data.fraDato)} />
				<TitleValue title="Til dato" value={Formatters.formatDate(data.tilDato)} />
			</div>
		</>
	)
}
