import React from 'react'
import Knapp from 'nav-frontend-knapper'
import NavButton from '~/components/ui/button/NavButton/NavButton'

import './Navigation.less'

export const Navigation = ({ showPrevious, onPrevious, isLastStep, formikBag }) => {
	const { isSubmitting, handleSubmit } = formikBag

	const onAbort = () => {
		console.log('handleAbort() - flytt denn til en logisk plass')
	}

	return (
		<div className="step-navknapper-wrapper">
			<div className="step-navknapper">
				<Knapp type="standard" onClick={onAbort}>
					AVBRYT
				</Knapp>
				<div className="step-navknapper--right">
					{showPrevious && <NavButton direction="backward" onClick={onPrevious} />}
					{!isLastStep && (
						<NavButton direction="forward" disabled={isSubmitting} onClick={handleSubmit} />
					)}
					{isLastStep && (
						<Knapp type="hoved" onClick={handleSubmit} disabled={isSubmitting}>
							OPPRETT
						</Knapp>
					)}
				</div>
			</div>
		</div>
	)
}
