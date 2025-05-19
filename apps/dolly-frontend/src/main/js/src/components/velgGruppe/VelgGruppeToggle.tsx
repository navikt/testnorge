import React, { useState } from 'react'
import NyGruppe from './NyGruppe'
import EksisterendeGruppe from '@/components/velgGruppe/EksisterendeGruppe'
import { ToggleGroup } from '@navikt/ds-react'
import styled from 'styled-components'
import { TestComponentSelectors } from '#/mocks/Selectors'
import AlleGrupper from '@/components/velgGruppe/AlleGrupper'
import { useFormContext } from 'react-hook-form'
import { runningE2ETest } from '@/service/services/Request'

interface VelgGruppeToggleProps {
	fraGruppe?: number
	grupper?: any
}

export enum Gruppevalg {
	MINE = 'Mine',
	ALLE = 'Alle',
	NY = 'Ny',
}

const StyledToggleGroup = styled(ToggleGroup)`
	margin-bottom: 10px;
`

export const VelgGruppeToggle = ({ fraGruppe, grupper }: VelgGruppeToggleProps) => {
	const formMethods = useFormContext()

	const harGrupper = grupper?.antallElementer > 0
	const harBrukerValg = formMethods?.watch('bruker')
	const [gruppevalg, setGruppevalg] = useState(
		harBrukerValg
			? Gruppevalg.ALLE
			: harGrupper || runningE2ETest()
				? Gruppevalg.MINE
				: Gruppevalg.NY,
	)

	const handleToggleChange = (value: Gruppevalg) => {
		setGruppevalg(value)
		formMethods.clearErrors(['gruppeId', 'bruker', 'gruppeNavn', 'gruppeHensikt'])
		formMethods.setValue('gruppeId', '')
		if (value === Gruppevalg.ALLE) {
			formMethods.setValue('bruker', '')
		} else {
			formMethods.setValue('bruker', undefined)
		}
		if (value === Gruppevalg.NY) {
			formMethods.setValue('gruppeNavn', '')
			formMethods.setValue('gruppeHensikt', '')
		} else {
			formMethods.setValue('gruppeNavn', undefined)
			formMethods.setValue('gruppeHensikt', undefined)
		}
	}

	return (
		<div className="toggle--wrapper">
			<StyledToggleGroup size={'small'} value={gruppevalg} onChange={handleToggleChange}>
				<ToggleGroup.Item
					data-testid={TestComponentSelectors.TOGGLE_EKSISTERENDE_GRUPPE}
					key={Gruppevalg.MINE}
					value={Gruppevalg.MINE}
					style={{ padding: '0 20px' }}
				>
					Mine grupper
				</ToggleGroup.Item>
				<ToggleGroup.Item
					data-testid={TestComponentSelectors.TOGGLE_ALLE_GRUPPER}
					key={Gruppevalg.ALLE}
					value={Gruppevalg.ALLE}
				>
					Alle grupper
				</ToggleGroup.Item>
				<ToggleGroup.Item
					data-testid={TestComponentSelectors.TOGGLE_NY_GRUPPE}
					key={Gruppevalg.NY}
					value={Gruppevalg.NY}
				>
					Ny gruppe
				</ToggleGroup.Item>
			</StyledToggleGroup>

			{gruppevalg === Gruppevalg.MINE && (
				<EksisterendeGruppe fraGruppe={fraGruppe} grupper={grupper} />
			)}
			{gruppevalg === Gruppevalg.ALLE && <AlleGrupper fraGruppe={fraGruppe} />}
			{gruppevalg === Gruppevalg.NY && <NyGruppe />}
		</div>
	)
}
