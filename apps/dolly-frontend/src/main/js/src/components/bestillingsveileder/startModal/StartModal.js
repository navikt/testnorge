import React, { useState } from 'react'
import DollyModal from '~/components/ui/modal/DollyModal'
import { NyIdent } from './NyIdent/NyIdent'
import { EksisterendeIdent } from './EksisterendeIdent/EksisterendeIdent'

import './startModal.less'
import { Radio, RadioGroup } from '@navikt/ds-react'

export const BestillingsveilederModal = ({ onAvbryt, onSubmit, brukernavn }) => {
	const [type, setType] = useState('ny')
	return (
		<DollyModal isOpen closeModal={onAvbryt} width="60%" overflow="auto">
			<div className="start-bestilling-modal">
				<h1>Opprett personer</h1>
				<RadioGroup
					name="eksisterende"
					legend="Type bestilling"
					checked={type}
					onChange={(e) => setType(e.target.value)}
				>
					<Radio value={'ny'} id={'ny'}>
						Ny person
					</Radio>
					<Radio value={'eksisterende'} id={'eksisterende'}>
						Eksisterende person
					</Radio>
				</RadioGroup>
				{type === 'ny' && <NyIdent onAvbryt={onAvbryt} onSubmit={onSubmit} zBruker={brukernavn} />}
				{type === 'eksisterende' && <EksisterendeIdent onAvbryt={onAvbryt} onSubmit={onSubmit} />}
			</div>
		</DollyModal>
	)
}
