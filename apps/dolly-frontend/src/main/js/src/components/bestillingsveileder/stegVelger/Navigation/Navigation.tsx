import React, { useContext } from 'react'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import { harAvhukedeAttributter } from '@/components/bestillingsveileder/utils'
import './Navigation.less'
import { AvbrytButton } from '@/components/ui/button/AvbrytButton/AvbrytButton'
import { useNavigate } from 'react-router'
import { TestComponentSelectors } from '#/mocks/Selectors'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { useFormContext, useWatch } from 'react-hook-form'
import { ShowErrorContext, ShowErrorContextType } from '@/components/bestillingsveileder/ShowErrorContext'

interface NavigationProps {
	step: number
	onPrevious: () => void
	isLastStep: boolean
	mutateLoading?: boolean
	handleSubmit: () => void
}

export const Navigation: React.FC<NavigationProps> = ({
	step,
	onPrevious,
	isLastStep,
	mutateLoading,
	handleSubmit,
}) => {
	const showPrevious = step > 0
	const errorContext: ShowErrorContextType = useContext(ShowErrorContext)
	const opts: BestillingsveilederContextType | any = useContext(BestillingsveilederContext)
	const importTestnorge = opts?.is?.importTestnorge
	const navigate = useNavigate()
	const formMethods = useFormContext()
	const {
		formState: { isSubmitting },
	} = formMethods

	const formValues = useWatch()
	const disabledVidere = step !== 0 && opts?.is?.leggTil && !harAvhukedeAttributter(formValues)

	const onAbort = () => navigate(-1)

	const getLastButtonText = () => {
		if (importTestnorge) {
			if (harAvhukedeAttributter(formValues)) {
				return 'Importer og opprett'
			}
			return 'Importer'
		}
		return 'Opprett'
	}

	return (
		<div className="step-navknapper-wrapper">
			<div className="step-navknapper">
				<AvbrytButton action={onAbort}>
					Er du sikker på at du vil avbryte bestillingen?
				</AvbrytButton>
				<div className="step-navknapper--right">
					{showPrevious && (
						<NavButton
							data-testid={TestComponentSelectors.BUTTON_TILBAKE}
							variant="secondary"
							onClick={onPrevious}
						>
							Tilbake
						</NavButton>
					)}
					{!isLastStep && (
						<NavButton
							data-testid={TestComponentSelectors.BUTTON_VIDERE}
							variant="primary"
							loading={mutateLoading}
							disabled={isSubmitting || disabledVidere}
							onClick={handleSubmit}
							style={{ verticalAlign: 'bottom' }}
						>
							Videre
						</NavButton>
					)}
					{isLastStep && (
						<NavButton
							data-testid={TestComponentSelectors.BUTTON_FULLFOER_BESTILLING}
							variant="primary"
							onClick={() => {
								errorContext?.setShowError(true)
								formMethods.trigger().then((valid) => {
									if (!valid) {
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
