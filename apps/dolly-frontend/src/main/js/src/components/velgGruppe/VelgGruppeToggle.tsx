import React, { useState } from 'react'
import NyGruppe from './NyGruppe'
import EksisterendeGruppe from '~/components/velgGruppe/EksisterendeGruppe'
import { ToggleGroup } from '@navikt/ds-react'

interface VelgGruppeToggleProps {
	setValgtGruppe: React.Dispatch<React.SetStateAction<string>>
	valgtGruppe: string
}

enum Gruppevalg {
	EKSISTERENDE = 'Eksisterende',
	NY = 'Ny',
}

export const VelgGruppeToggle = ({ setValgtGruppe, valgtGruppe }: VelgGruppeToggleProps) => {
	const [gruppevalg, setGruppevalg] = useState(Gruppevalg.EKSISTERENDE)

	const handleToggleChange = (value: Gruppevalg) => {
		setGruppevalg(value)
		setValgtGruppe('')
	}
	return (
		<div className="toggle--wrapper">
			<ToggleGroup size={'small'} value={gruppevalg} onChange={handleToggleChange}>
				<ToggleGroup.Item key={Gruppevalg.EKSISTERENDE} value={Gruppevalg.EKSISTERENDE}>
					Eksisterende gruppe
				</ToggleGroup.Item>
				<ToggleGroup.Item key={Gruppevalg.NY} value={Gruppevalg.NY}>
					Ny gruppe
				</ToggleGroup.Item>
			</ToggleGroup>

			{gruppevalg === Gruppevalg.EKSISTERENDE ? (
				<EksisterendeGruppe setValgtGruppe={setValgtGruppe} valgtGruppe={valgtGruppe} />
			) : (
				<NyGruppe setValgtGruppe={setValgtGruppe} />
			)}
		</div>
	)
}
