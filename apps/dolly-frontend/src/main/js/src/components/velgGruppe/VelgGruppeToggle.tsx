import React from 'react'
import NyGruppe from './NyGruppe'
import EksisterendeGruppe from '@/components/velgGruppe/EksisterendeGruppe'
import { ToggleGroup } from '@navikt/ds-react'
import styled from 'styled-components'
import { CypressSelector } from '../../../cypress/mocks/Selectors'
import AlleGrupper from '@/components/velgGruppe/AlleGrupper'

interface VelgGruppeToggleProps {
	setValgtGruppe: React.Dispatch<React.SetStateAction<string>>
	valgtGruppe: string
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
	setValgtGruppe,
	valgtGruppe,
	fraGruppe = null,
	gruppevalg,
	setGruppevalg,
}: VelgGruppeToggleProps) => {
	const handleToggleChange = (value: Gruppevalg) => {
		setGruppevalg(value)
		setValgtGruppe('')
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
				<ToggleGroup.Item key={Gruppevalg.ALLE} value={Gruppevalg.ALLE}>
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

			{gruppevalg === Gruppevalg.MINE && (
				<EksisterendeGruppe
					setValgtGruppe={setValgtGruppe}
					valgtGruppe={valgtGruppe}
					fraGruppe={fraGruppe}
				/>
			)}
			{gruppevalg === Gruppevalg.ALLE && (
				<AlleGrupper
					setValgtGruppe={setValgtGruppe}
					valgtGruppe={valgtGruppe}
					fraGruppe={fraGruppe}
				/>
			)}
			{gruppevalg === Gruppevalg.NY && <NyGruppe setValgtGruppe={setValgtGruppe} />}
		</div>
	)
}
