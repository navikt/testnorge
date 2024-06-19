import Icon from '@/components/ui/icon/Icon'
import { ToggleGroup } from '@navikt/ds-react'
import React from 'react'
import styled from 'styled-components'
import { TestComponentSelectors } from '#/mocks/Selectors'

type Props = {
	soekValg: SoekTypeValg
	setValgtSoekType: React.Dispatch<React.SetStateAction<string>>
}

const StyledItem = styled(ToggleGroup.Item)`
	padding-right: 8px;
	padding-left: 8px;
`

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
				<StyledItem
					key={SoekTypeValg.PERSON}
					data-testid={TestComponentSelectors.TOGGLE_SEARCH_PERSON}
					value={SoekTypeValg.PERSON}
				>
					<Icon kind={soekValg === SoekTypeValg.PERSON ? 'man-light' : 'man'} />
				</StyledItem>
				<StyledItem
					key={SoekTypeValg.BESTILLING}
					data-testid={TestComponentSelectors.TOGGLE_SEARCH_BESTILLING}
					value={SoekTypeValg.BESTILLING}
				>
					<Icon kind={soekValg === SoekTypeValg.BESTILLING ? 'bestilling-light' : 'bestilling'} />
				</StyledItem>
			</ToggleGroup>
		</div>
	)
}
