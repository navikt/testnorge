import React from 'react'
import NyGruppe from './NyGruppe'
import EksisterendeGruppe from '@/components/velgGruppe/EksisterendeGruppe'
import { ToggleGroup } from '@navikt/ds-react'
import styled from 'styled-components'
import { CypressSelector } from '../../../cypress/mocks/Selectors'
import AlleGrupper from '@/components/velgGruppe/AlleGrupper'

interface VelgGruppeToggleProps {
	fraGruppe?: number
}

export enum Gruppevalg {
	MINE = 'Mine',
	ALLE = 'Alle',
	NY = 'Ny',
}

const StyledToggleGroup = styled(ToggleGroup)`
	margin-bottom: 10px;
`

export const VelgGruppeToggle = ({
	fraGruppe = null,
	gruppevalg,
	setGruppevalg,
}: VelgGruppeToggleProps) => {
	const handleToggleChange = (value: Gruppevalg) => {
		setGruppevalg(value)
	}
	return (
		<div className="toggle--wrapper">
			<StyledToggleGroup size={'small'} value={gruppevalg} onChange={handleToggleChange}>
				<ToggleGroup.Item
					data-cy={CypressSelector.TOGGLE_EKSISTERENDE_GRUPPE}
					key={Gruppevalg.MINE}
					value={Gruppevalg.MINE}
					style={{ padding: '0 20px' }}
				>
					Mine grupper
				</ToggleGroup.Item>
				<ToggleGroup.Item
					data-cy={CypressSelector.TOGGLE_ALLE_GRUPPER}
					key={Gruppevalg.ALLE}
					value={Gruppevalg.ALLE}
				>
					Alle grupper
				</ToggleGroup.Item>
				<ToggleGroup.Item
					data-cy={CypressSelector.TOGGLE_NY_GRUPPE}
					key={Gruppevalg.NY}
					value={Gruppevalg.NY}
				>
					Ny gruppe
				</ToggleGroup.Item>
			</StyledToggleGroup>

			{gruppevalg === Gruppevalg.MINE && <EksisterendeGruppe fraGruppe={fraGruppe} />}
			{gruppevalg === Gruppevalg.ALLE && <AlleGrupper fraGruppe={fraGruppe} />}
			{gruppevalg === Gruppevalg.NY && <NyGruppe />}
		</div>
	)
}
