import React, { useState } from 'react'
import { RadioPanelGruppe } from 'nav-frontend-skjema'
import SoekMiniNorge from '~/pages/soekMiniNorge/SoekMiniNorge'
import TestnorgePage from '~/pages/testnorgePage'

type Selected = 'MININORGE' | 'TESTNORGE'

type Props = {
	brukertype: string
}

export default ({ brukertype }: Props) => {
	const [selected, setSelected] = useState<Selected>('TESTNORGE')
	const bankIdBruker = brukertype === 'BANKID'
	return (
		<div>
			{!bankIdBruker && (
				<RadioPanelGruppe
					legend="Hvilket datasett ønsker du å søke i?"
					radios={[
						{ label: 'Testnorge', value: 'TESTNORGE' },
						{ label: 'Mini-Norge', value: 'MININORGE' },
					]}
					checked={selected}
					// @ts-ignore
					onChange={(value) => setSelected(value.target.value)}
					name="velg_datasett"
				/>
			)}
			{selected === 'TESTNORGE' && <TestnorgePage />}
			{!bankIdBruker && selected === 'MININORGE' && <SoekMiniNorge />}
		</div>
	)
}
