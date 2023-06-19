import Icon from '@/components/ui/icon/Icon'
import { ToggleGroup } from '@navikt/ds-react'
import React from 'react'

type Props = {
	soekValg: SoekTypeValg
	setValgtSoekType: React.Dispatch<React.SetStateAction<string>>
}

const ICONSIZE = 16

export enum SoekTypeValg {
	PERSON = 'Person',
	BESTILLING = 'Bestilling',
}

export const VelgSoekTypeToggle = ({ soekValg, setValgtSoekType }: Props) => {
	const handleToggleChange = (value: string) => {
		setValgtSoekType(value)
	}
	return (
		<div className="toggle--wrapper">
			<ToggleGroup
				onChange={handleToggleChange}
				defaultValue={SoekTypeValg.PERSON}
				style={{ backgroundColor: '#ffffff' }}
			>
				<ToggleGroup.Item key={SoekTypeValg.PERSON} value={SoekTypeValg.PERSON}>
					<Icon kind={soekValg === SoekTypeValg.PERSON ? 'manLight' : 'man'} size={ICONSIZE} />
				</ToggleGroup.Item>
				<ToggleGroup.Item key={SoekTypeValg.BESTILLING} value={SoekTypeValg.BESTILLING}>
					<Icon
						kind={soekValg === SoekTypeValg.BESTILLING ? 'bestillingLight' : 'bestilling'}
						size={ICONSIZE}
					/>
				</ToggleGroup.Item>
			</ToggleGroup>
		</div>
	)
}
