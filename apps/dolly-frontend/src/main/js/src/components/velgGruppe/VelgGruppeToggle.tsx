import React, { useState } from 'react'
import EksisterendeGruppeConnector from './EksisterendeGruppeConnector'
import NyGruppe from './NyGruppe'
import { ToggleGroup } from '~/components/ui/toggle/Toggle'

interface VelgGruppeToggleProps {
	setValgtGruppe: React.Dispatch<React.SetStateAction<string>>
	valgtGruppe: string
}

const togglenavn = 'Gruppevalgtoggle'

enum Gruppevalg {
	EKSISTERENDE = 'Eksisterende',
	NY = 'Ny',
}

export const VelgGruppeToggle = ({ setValgtGruppe, valgtGruppe }: VelgGruppeToggleProps) => {
	const [gruppevalg, setGruppevalg] = useState(Gruppevalg.EKSISTERENDE)

	const handleToggleChange = (e: React.ChangeEvent<any>) => {
		setGruppevalg(e.target.value)
		setValgtGruppe('')
	}
	return (
		<div className="toggle--wrapper">
			<ToggleGroup
				onChange={handleToggleChange}
				name={togglenavn}
				defaultValue={Gruppevalg.EKSISTERENDE}
			>
				<ToggleGroup.Item key={Gruppevalg.EKSISTERENDE} value={Gruppevalg.EKSISTERENDE}>
					Eksisterende gruppe
				</ToggleGroup.Item>
				<ToggleGroup.Item key={Gruppevalg.NY} value={Gruppevalg.NY}>
					Ny gruppe
				</ToggleGroup.Item>
			</ToggleGroup>

			{gruppevalg === Gruppevalg.EKSISTERENDE ? (
				<EksisterendeGruppeConnector setValgtGruppe={setValgtGruppe} valgtGruppe={valgtGruppe} />
			) : (
				<NyGruppe setValgtGruppe={setValgtGruppe} />
			)}
		</div>
	)
}
