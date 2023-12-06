import React, { useContext } from 'react'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import { harAvhukedeAttributter } from '@/components/bestillingsveileder/utils'

import './Navigation.less'
import { AvbrytButton } from '@/components/ui/button/AvbrytButton/AvbrytButton'
import { useNavigate } from 'react-router-dom'
import { CypressSelector } from '../../../../../cypress/mocks/Selectors'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { useFormContext } from 'react-hook-form'
import _ from 'lodash'

export const Navigation = ({ step, onPrevious, isLastStep, handleSubmit }) => {
	const showPrevious = step > 0
	const opts = useContext(BestillingsveilederContext)
	const importTestnorge = opts?.is?.importTestnorge

	const navigate = useNavigate()
	const {
		getValues,
		formState: { errors, isSubmitting },
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

	const hasErrors = step === 1 && !_.isEmpty(errors)
	const disabledVidere = step === 1 && opts?.is?.leggTil && !harAvhukedeAttributter(getValues())

	return (
		<div className="step-navknapper-wrapper">
			<div className="step-navknapper">
				<AvbrytButton action={onAbort}>
					Er du sikker på at du vil avbryte bestillingen?
				</AvbrytButton>

				<div className="step-navknapper--right">
					{showPrevious && (
						<NavButton
							data-cy={CypressSelector.BUTTON_TILBAKE}
							variant={'secondary'}
							onClick={() => onPrevious()}
						>
							Tilbake
						</NavButton>
					)}
					{!isLastStep && (
						<NavButton
							data-cy={CypressSelector.BUTTON_VIDERE}
							variant={'primary'}
							disabled={isSubmitting || disabledVidere}
							onClick={hasErrors ? () => console.error('Feil i skjemaet') : handleSubmit}
						>
							Videre
						</NavButton>
					)}
					{isLastStep && (
						<NavButton
							data-cy={CypressSelector.BUTTON_FULLFOER_BESTILLING}
							variant={'primary'}
							onClick={handleSubmit}
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
