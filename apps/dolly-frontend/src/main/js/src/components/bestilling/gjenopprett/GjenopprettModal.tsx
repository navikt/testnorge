import { DollyModal } from '@/components/ui/modal/DollyModal'
import { filterMiljoe } from '@/components/miljoVelger/MiljoeInfo'
import React, { Fragment } from 'react'
import { MiljoVelger } from '@/components/miljoVelger/MiljoVelger'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import * as yup from 'yup'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { useDollyEnvironments } from '@/utils/hooks/useEnvironments'
import { FormProvider, useForm } from 'react-hook-form'
import { yupResolver } from '@hookform/resolvers/yup'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { Alert } from '@navikt/ds-react'

type GjenopprettModalProps = {
	gjenopprettHeader: any
	environments?: Array<string>
	submitForm: any
	closeModal: any
	bestilling?: any
	brukertype?: string
}

export const GjenopprettModal = ({
	gjenopprettHeader,
	environments,
	submitForm,
	closeModal,
	bestilling,
	brukertype,
}: GjenopprettModalProps) => {
	const { dollyEnvironments: tilgjengeligeEnvironments } = useDollyEnvironments()
	const schemaValidation = yup.object().shape({
		environments: yup.array().required('Velg minst ett miljø'),
	})
	const formMethods = useForm({
		mode: 'onBlur',
		defaultValues: { environments: filterMiljoe(tilgjengeligeEnvironments, environments) },
		resolver: yupResolver(schemaValidation),
	})

	const getWarningText = () => {
		if (
			bestilling?.bestilling?.arbeidsplassenCV &&
			(!bestilling?.bestilling?.arenaforvalter || !bestilling?.bestilling?.arbeidssoekerregisteret)
		) {
			return 'Gjenoppretting av Nav CV vil ikke fungere om person mangler oppføring i Arbeidssøkerregisteret og Arena.'
		}
		return null
	}

	const warningText = getWarningText()

	return (
		<FormProvider {...formMethods}>
			<DollyModal isOpen={true} closeModal={closeModal} width="50%" overflow="auto">
				<ErrorBoundary>
					{gjenopprettHeader}
					<Fragment>
						<MiljoVelger
							bestillingsdata={bestilling ? bestilling.bestilling : null}
							heading="Velg miljø å gjenopprette i"
							bankIdBruker={brukertype && brukertype === 'BANKID'}
							alleredeValgtMiljoe={[]}
						/>
						{warningText && (
							<div style={{ padding: '0 20px' }}>
								<Alert variant={'warning'}>{warningText}</Alert>
							</div>
						)}
						<div className="dollymodal_buttons">
							<NavButton variant={'danger'} onClick={closeModal}>
								Avbryt
							</NavButton>
							<NavButton
								data-testid={TestComponentSelectors.BUTTON_BESTILLINGDETALJER_GJENOPPRETT_UTFOER}
								variant={'primary'}
								onClick={formMethods.handleSubmit(submitForm)}
							>
								Utfør
							</NavButton>
						</div>
					</Fragment>
				</ErrorBoundary>
			</DollyModal>
		</FormProvider>
	)
}
