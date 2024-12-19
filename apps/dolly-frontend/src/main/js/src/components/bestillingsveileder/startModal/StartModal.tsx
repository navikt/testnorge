import React, { useState } from 'react'
import { DollyModal } from '@/components/ui/modal/DollyModal'
import { NyBestillingProps, NyIdent } from './NyIdent/NyIdent'
import { EksisterendeIdent } from './EksisterendeIdent/EksisterendeIdent'
import styled from 'styled-components'
import './startModal.less'
import { ToggleGroup } from '@navikt/ds-react'
import { TestComponentSelectors } from '#/mocks/Selectors'

const StyledToggleGroup = styled(ToggleGroup)`
	margin-top: 25px;
	margin-bottom: 20px;
`

const BestillingsveilederModal = ({ onAvbryt, onSubmit, brukernavn }: NyBestillingProps) => {
	const [type, setType] = useState('ny')
	return (
		<DollyModal isOpen closeModal={onAvbryt} width="60%" overflow="auto">
			<div className="start-bestilling-modal">
				<h1>Opprett personer</h1>
				<StyledToggleGroup value={type} onChange={(value) => setType(value)}>
					<ToggleGroup.Item
						data-testid={TestComponentSelectors.TOGGLE_NY_PERSON}
						value={'ny'}
						key={'ny'}
					>
						Ny person
					</ToggleGroup.Item>
					<ToggleGroup.Item
						data-testid={TestComponentSelectors.TOGGLE_EKSISTERENDE_PERSON}
						value={'eksisterende'}
						key={'eksisterende'}
					>
						Eksisterende person
					</ToggleGroup.Item>
				</StyledToggleGroup>
				{type === 'ny' && (
					<NyIdent onAvbryt={onAvbryt} onSubmit={onSubmit} brukernavn={brukernavn} />
				)}
				{type === 'eksisterende' && <EksisterendeIdent onAvbryt={onAvbryt} onSubmit={onSubmit} />}
			</div>
		</DollyModal>
	)
}

export default BestillingsveilederModal
