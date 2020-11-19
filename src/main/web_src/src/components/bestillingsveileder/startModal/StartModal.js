import React, { useState } from 'react'
import { RadioPanelGruppe } from 'nav-frontend-skjema'
import DollyModal from '~/components/ui/modal/DollyModal'
import { NyIdent } from './NyIdent/NyIdent'
import { EksisterendeIdent } from './EksisterendeIdent/EksisterendeIdent'

import './startModal.less'

export const BestillingsveilederModal = ({ onAvbryt, onSubmit, brukernavn }) => {
	const [type, setType] = useState('ny')
	return (
		<DollyModal isOpen closeModal={onAvbryt} width="60%" overflow="auto">
			<div className="start-bestilling-modal">
				<h1>Opprett personer</h1>

				<RadioPanelGruppe
					name="eksisterende"
					legend="Type bestilling"
					radios={[
						{ label: 'Ny person', value: 'ny', id: 'ny' },
						{ label: 'Eksisterende person', value: 'eksisterende', id: 'eksisterende' }
					]}
					checked={type}
					onChange={e => setType(e.target.value)}
				/>

				{type === 'ny' && <NyIdent onAvbryt={onAvbryt} onSubmit={onSubmit} zBruker={brukernavn} />}
				{type === 'eksisterende' && <EksisterendeIdent onAvbryt={onAvbryt} onSubmit={onSubmit} />}
			</div>
		</DollyModal>
	)
}
