import React from 'react'
import NavButton from '~/components/ui/button/NavButton/NavButton'

export const ModalActions = ({ disabled, onAvbryt, onSubmit }) => (
	<div className="start-bestilling-modal_actions">
		<NavButton onClick={onAvbryt}>Avbryt</NavButton>
		<NavButton type="hoved" onClick={onSubmit} disabled={disabled}>
			Start bestilling
		</NavButton>
	</div>
)
