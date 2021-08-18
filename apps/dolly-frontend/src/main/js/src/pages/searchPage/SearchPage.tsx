import React, { useState } from 'react'
import { RadioPanelGruppe } from 'nav-frontend-skjema'
import SoekMiniNorge from '~/pages/soekMiniNorge/SoekMiniNorge'
import TestnorgePage from '~/pages/testnorgePage'

type Selected = 'MININORGE' | 'TESTNORGE'

export default () => {
	const [selected, setSelected] = useState<Selected>('TESTNORGE')

	return (
		<div>
			<RadioPanelGruppe
				legend="Hvilket datasett ønsker du å søke i?"
				radios={[
					{ label: 'Testnorge', value: 'TESTNORGE' },
					{ label: 'Mini-Norge', value: 'MININORGE' }
				]}
				checked={selected}
				// @ts-ignore
				onChange={value => setSelected(value.target.value)}
				name="velg_datasett"
			/>
			{selected === 'TESTNORGE' && <TestnorgePage />}
			{selected === 'MININORGE' && <SoekMiniNorge />}
		</div>
	)
}
