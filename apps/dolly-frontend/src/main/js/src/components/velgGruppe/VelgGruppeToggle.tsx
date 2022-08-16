import React, { useState } from 'react'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import NyGruppe from './NyGruppe'
import EksisterendeGruppe from '~/components/velgGruppe/EksisterendeGruppe'

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
			<ToggleGruppe onChange={handleToggleChange} name={togglenavn}>
				<ToggleKnapp
					key={Gruppevalg.EKSISTERENDE}
					value={Gruppevalg.EKSISTERENDE}
					checked={gruppevalg === Gruppevalg.EKSISTERENDE}
				>
					Eksisterende gruppe
				</ToggleKnapp>
				<ToggleKnapp
					key={Gruppevalg.NY}
					value={Gruppevalg.NY}
					checked={gruppevalg === Gruppevalg.NY}
				>
					Ny gruppe
				</ToggleKnapp>
			</ToggleGruppe>

			{gruppevalg === Gruppevalg.EKSISTERENDE ? (
				<EksisterendeGruppe setValgtGruppe={setValgtGruppe} valgtGruppe={valgtGruppe} />
			) : (
				<NyGruppe setValgtGruppe={setValgtGruppe} />
			)}
		</div>
	)
}
