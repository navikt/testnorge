import React, { useContext } from 'react'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import { harAvhukedeAttributter } from '~/components/bestillingsveileder/utils'

import './Navigation.less'
import { AvbrytButton } from '~/components/ui/button/AvbrytButton/AvbrytButton'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'
import { useNavigate } from 'react-router-dom'
import { setNestedObjectValues } from 'formik'

export const Navigation = ({ step, onPrevious, isLastStep, formikBag }) => {
	const showPrevious = step > 0
	const opts = useContext(BestillingsveilederContext)
	const importTestnorge = opts.is.importTestnorge

	const navigate = useNavigate()
	const { isSubmitting, handleSubmit, setTouched, errors } = formikBag

	const onAbort = () => navigate(-1)

	const getLastButtonText = () => {
		if (importTestnorge) {
			if (harAvhukedeAttributter(formikBag.values)) {
				return 'Importer og opprett'
			} else {
				return 'Importer'
			}
		}
		return 'Opprett'
	}

	const hasInntektstubError = step === 1 && formikBag?.errors?.hasOwnProperty('inntektstub')
	const hasAaregError = step === 1 && formikBag?.errors?.hasOwnProperty('aareg')
	const disabledVidere = step === 1 && opts.is.leggTil && !harAvhukedeAttributter(formikBag.values)

	return (
		<div className="step-navknapper-wrapper">
			<div className="step-navknapper">
				<AvbrytButton action={onAbort}>
					Er du sikker på at du vil avbryte bestillingen?
				</AvbrytButton>

				<div className="step-navknapper--right">
					{showPrevious && <NavButton onClick={() => onPrevious(formikBag)}>Tilbake</NavButton>}
					{!isLastStep && (
						<NavButton
							variant={'primary'}
							disabled={isSubmitting || disabledVidere}
							onClick={
								hasInntektstubError || hasAaregError
									? () => setTouched(setNestedObjectValues(errors, true))
									: handleSubmit
							}
						>
							Videre
						</NavButton>
					)}
					{isLastStep && (
						<NavButton variant={'primary'} onClick={handleSubmit} disabled={isSubmitting}>
							{getLastButtonText()}
						</NavButton>
					)}
				</div>
			</div>
		</div>
	)
}
