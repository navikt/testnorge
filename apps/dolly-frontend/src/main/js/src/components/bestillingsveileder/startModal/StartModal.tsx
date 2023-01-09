import React, { useState } from 'react'
import DollyModal from '@/components/ui/modal/DollyModal'
import { NyBestillingProps, NyIdent } from './NyIdent/NyIdent'
import { EksisterendeIdent } from './EksisterendeIdent/EksisterendeIdent'
import styled from 'styled-components'

import './startModal.less'

import { ToggleGroup } from '@navikt/ds-react'

const StyledToggleGroup = styled(ToggleGroup)`
	margin-top: 25px;
	margin-bottom: 20px;
`

export const BestillingsveilederModal = ({ onAvbryt, onSubmit, brukernavn }: NyBestillingProps) => {
	const [type, setType] = useState('ny')
	return (
		<DollyModal isOpen closeModal={onAvbryt} width="60%" overflow="auto">
			<div className="start-bestilling-modal">
				<h1>Opprett personer</h1>
				<StyledToggleGroup size={'small'} value={type} onChange={(value) => setType(value)}>
					<ToggleGroup.Item value={'ny'} key={'ny'}>
						Ny person
					</ToggleGroup.Item>
					<ToggleGroup.Item value={'eksisterende'} key={'eksisterende'}>
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
