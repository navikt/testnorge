import React from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'

export type UtvistMedInnreiseForbud = {
	utvistMedInnreiseForbud: {
		innreiseForbud: string
		innreiseForbudVedtaksDato: string
		varighet: string
	}
}

export const UtvistMedInnreiseForbudVisning = ({
	utvistMedInnreiseForbud
}: UtvistMedInnreiseForbud) =>
	utvistMedInnreiseForbud && Object.values(utvistMedInnreiseForbud).some(item => item !== null) ? (
		<>
			<h4>Utvist med innreiseforbud</h4>
			<div className="person-visning_content">
				<TitleValue
					title="Innreiseforbud"
					value={Formatters.showLabel('jaNeiUavklart', utvistMedInnreiseForbud.innreiseForbud)}
				/>
				<TitleValue
					title="Innreiseforbud vedtatt"
					value={Formatters.formatDate(utvistMedInnreiseForbud.innreiseForbudVedtaksDato)}
				/>
				<TitleValue
					title="Varighet"
					value={Formatters.showLabel('varighet', utvistMedInnreiseForbud.varighet)}
				/>
			</div>
		</>
	) : null
