import React, { useContext } from 'react'
import { useDispatch } from 'react-redux'
import { go } from 'connected-react-router'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import { harAvhukedeAttributter } from '~/components/bestillingsveileder/utils'

import './Navigation.less'
import { AvbrytButton } from '~/components/ui/button/AvbrytButton/AvbrytButton'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'

export const Navigation = ({ showPrevious, onPrevious, isLastStep, formikBag }) => {
	const opts = useContext(BestillingsveilederContext)
	const importTestnorge = opts.is.importTestnorge

	const dispatch = useDispatch()
	const { isSubmitting, handleSubmit } = formikBag

	const onAbort = () => dispatch(go(-1))

	const getLastButtonText = () => {
		if (importTestnorge) {
			if (harAvhukedeAttributter(formikBag.values)) {
				return 'IMPORTER OG OPPRETT'
			} else {
				return 'IMPORTER'
			}
		}
		return 'OPPRETT'
	}

	return (
		<div className="step-navknapper-wrapper">
			<div className="step-navknapper">
				<AvbrytButton action={onAbort}>
					Er du sikker på at du vil avbryte bestillingen?
				</AvbrytButton>

				<div className="step-navknapper--right">
					{showPrevious && <NavButton onClick={onPrevious}>Tilbake</NavButton>}
					{!isLastStep && (
						<NavButton type="hoved" disabled={isSubmitting} onClick={handleSubmit}>
							Videre
						</NavButton>
					)}
					{isLastStep && (
						<NavButton type="hoved" onClick={handleSubmit} disabled={isSubmitting}>
							{getLastButtonText()}
						</NavButton>
					)}
				</div>
			</div>
		</div>
	)
}
