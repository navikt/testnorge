import React, { useState } from 'react'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import Icon from '~/components/ui/icon/Icon'

type Props = {
	setValgtSoekType: React.Dispatch<React.SetStateAction<string>>
}

const ICONSIZE = 16
const togglenavn = 'soekToggle'

export enum SoekTypeValg {
	PERSON = 'Person',
	BESTILLING = 'Bestilling',
}

export const VelgSoekTypeToggle = ({ setValgtSoekType }: Props) => {
	const [soekValg, setSoekValg] = useState(SoekTypeValg.PERSON)

	const handleToggleChange = (e: React.ChangeEvent<any>) => {
		setSoekValg(e.target.value)
		setValgtSoekType(e.target.value)
	}
	return (
		<div className="toggle--wrapper">
			<ToggleGruppe onChange={handleToggleChange} name={togglenavn}>
				<ToggleKnapp
					key={SoekTypeValg.PERSON}
					value={SoekTypeValg.PERSON}
					checked={soekValg === SoekTypeValg.PERSON}
				>
					<Icon kind={soekValg === SoekTypeValg.PERSON ? 'manLight' : 'man'} size={ICONSIZE} />
				</ToggleKnapp>
				<ToggleKnapp
					key={SoekTypeValg.BESTILLING}
					value={SoekTypeValg.BESTILLING}
					checked={soekValg === SoekTypeValg.BESTILLING}
				>
					<Icon
						kind={soekValg === SoekTypeValg.BESTILLING ? 'bestillingLight' : 'bestilling'}
						size={ICONSIZE}
					/>
				</ToggleKnapp>
			</ToggleGruppe>
		</div>
	)
}
