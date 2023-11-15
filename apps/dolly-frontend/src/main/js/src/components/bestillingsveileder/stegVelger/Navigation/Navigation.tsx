import React, { useContext } from 'react'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import { harAvhukedeAttributter } from '@/components/bestillingsveileder/utils'

import './Navigation.less'
import { AvbrytButton } from '@/components/ui/button/AvbrytButton/AvbrytButton'
import { useNavigate } from 'react-router-dom'
import { CypressSelector } from '../../../../../cypress/mocks/Selectors'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { useForm } from 'react-hook-form'

export const Navigation = ({ step, onPrevious, isLastStep }) => {
	const showPrevious = step > 0
	const opts = useContext(BestillingsveilederContext)
	const importTestnorge = opts?.is?.importTestnorge

	const navigate = useNavigate()
	const {
		getValues,
		handleSubmit,
		formState: { errors, isSubmitting },
	} = useForm()

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

	const hasInntektstubError = step === 1 && errors?.hasOwnProperty('inntektstub')
	const hasAaregError = step === 1 && errors?.hasOwnProperty('aareg')
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
							onClick={
								hasInntektstubError || hasAaregError
									? () => console.error('Feil i skjemaet, må fikse denne')
									: //setTouched(setNestedObjectValues(errors, true)) TODO: FIKSE DENNE
									  handleSubmit
							}
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
