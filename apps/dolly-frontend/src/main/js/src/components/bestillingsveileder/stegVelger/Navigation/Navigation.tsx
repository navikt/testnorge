import React, { useContext } from 'react'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import { harAvhukedeAttributter } from '@/components/bestillingsveileder/utils'

import './Navigation.less'
import { AvbrytButton } from '@/components/ui/button/AvbrytButton/AvbrytButton'
import { useNavigate } from 'react-router-dom'
import { CypressSelector } from '../../../../../cypress/mocks/Selectors'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { useFormContext } from 'react-hook-form'
import {
	ShowErrorContext,
	ShowErrorContextType,
} from '@/components/bestillingsveileder/ShowErrorContext'

export const Navigation = ({ step, onPrevious, isLastStep, handleSubmit }) => {
	const showPrevious = step > 0
	const errorContext: ShowErrorContextType = useContext(ShowErrorContext)
	const opts: any = useContext(BestillingsveilederContext)
	const importTestnorge = opts?.is?.importTestnorge

	const navigate = useNavigate()
	const formMethods = useFormContext()
	const {
		getValues,
		formState: { isSubmitting },
	} = useFormContext()

	const onAbort = () => navigate(-1)

	const getLastButtonText = () => {
		if (importTestnorge) {
			if (harAvhukedeAttributter(getValues())) {
				return 'Importer og opprett'
			} else {
				return 'Importer'
			}
		}
		return 'Opprett'
	}

	const disabledVidere = step !== 0 && opts?.is?.leggTil && !harAvhukedeAttributter(getValues())

	return (
		<div className="step-navknapper-wrapper">
			<div className="step-navknapper">
				<AvbrytButton action={onAbort}>
					Er du sikker på at du vil avbryte bestillingen?
				</AvbrytButton>

				<div className="step-navknapper--right">
					{showPrevious && (
						<NavButton
							data-testid={CypressSelector.BUTTON_TILBAKE}
							variant={'secondary'}
							onClick={onPrevious}
						>
							Tilbake
						</NavButton>
					)}
					{!isLastStep && (
						<NavButton
							data-testid={CypressSelector.BUTTON_VIDERE}
							variant={'primary'}
							disabled={isSubmitting || disabledVidere}
							onClick={handleSubmit}
						>
							Videre
						</NavButton>
					)}
					{isLastStep && (
						<NavButton
							data-testid={CypressSelector.BUTTON_FULLFOER_BESTILLING}
							variant={'primary'}
							onClick={() => {
								errorContext?.setShowError(true)
								formMethods.trigger().then((valid) => {
									if (!valid) {
										console.warn('Feil i form')
										console.error(formMethods.formState.errors)
										return
									}
									handleSubmit()
								})
							}}
							disabled={isSubmitting}
						>
							{getLastButtonText()}
						</NavButton>
					)}
				</div>
			</div>
		</div>
	)
}
