import React, { useState } from 'react'
import NyGruppe from './NyGruppe'
import EksisterendeGruppe from '@/components/velgGruppe/EksisterendeGruppe'
import { ToggleGroup } from '@navikt/ds-react'
import styled from 'styled-components'

interface VelgGruppeToggleProps {
	setValgtGruppe: React.Dispatch<React.SetStateAction<string>>
	valgtGruppe: string
	fraGruppe?: number
}

enum Gruppevalg {
	EKSISTERENDE = 'Eksisterende',
	NY = 'Ny',
}

const StyledToggleGroup = styled(ToggleGroup)`
	margin-bottom: 10px;
`

export const VelgGruppeToggle = ({
	setValgtGruppe,
	valgtGruppe,
	fraGruppe = null,
}: VelgGruppeToggleProps) => {
	const [gruppevalg, setGruppevalg] = useState(Gruppevalg.EKSISTERENDE)

	const handleToggleChange = (value: Gruppevalg) => {
		setGruppevalg(value)
		setValgtGruppe('')
	}
	return (
		<div className="toggle--wrapper">
			<StyledToggleGroup size={'small'} value={gruppevalg} onChange={handleToggleChange}>
				<ToggleGroup.Item
					key={Gruppevalg.EKSISTERENDE}
					value={Gruppevalg.EKSISTERENDE}
					style={{ padding: '0 20px' }}
				>
					Eksisterende gruppe
				</ToggleGroup.Item>
				<ToggleGroup.Item key={Gruppevalg.NY} value={Gruppevalg.NY}>
					Ny gruppe
				</ToggleGroup.Item>
			</StyledToggleGroup>

			{gruppevalg === Gruppevalg.EKSISTERENDE ? (
				<EksisterendeGruppe
					setValgtGruppe={setValgtGruppe}
					valgtGruppe={valgtGruppe}
					fraGruppe={fraGruppe}
				/>
			) : (
				<NyGruppe setValgtGruppe={setValgtGruppe} />
			)}
		</div>
	)
}
