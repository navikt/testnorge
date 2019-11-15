import React from 'react'
import Knapp from 'nav-frontend-knapper'
import NavButton from '~/components/ui/button/NavButton/NavButton'

import './Navigation.less'

export const Navigation = ({ showPrevious, onPrevious, isLastStep, formikBag }) => {
	const { isValid, isSubmitting, handleSubmit } = formikBag
	const isDisabled = !isValid || isSubmitting

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
						<NavButton direction="forward" disabled={isDisabled} onClick={handleSubmit} />
					)}
					{isLastStep && (
						<Knapp type="hoved" onClick={handleSubmit} disabled={isDisabled}>
							OPPRETT
						</Knapp>
					)}
				</div>
			</div>
		</div>
	)
}
