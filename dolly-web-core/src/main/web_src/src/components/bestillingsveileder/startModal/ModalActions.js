import React from 'react'
import Knapp from 'nav-frontend-knapper'

export const ModalActions = ({ disabled, onAvbryt, onSubmit }) => (
	<div className="start-bestilling-modal_actions">
		<Knapp type="standard" onClick={onAvbryt}>
			Avbryt
		</Knapp>
		<Knapp type="hoved" onClick={onSubmit} disabled={disabled}>
			Start bestilling
		</Knapp>
	</div>
)
